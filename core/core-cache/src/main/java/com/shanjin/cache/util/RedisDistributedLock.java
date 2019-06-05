package com.shanjin.cache.util;

import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/18
 * @desc 基于redis 单线程 队列操作的 分布式锁
 * @see
 */
public class RedisDistributedLock {


    private static final int DEFAULT_TIME_OUT = 120;

    private static final String LOCK_PRE_KEY = "redis_dist_lock:";


    /**
     * lock 指定的key 异步锁，返回锁定状态，具体业务操作有具体业务逻辑基于结果值判断
     * 后期需要添加（同步）阻塞锁
     *
     * @param timeout 超时时间（s）防止死锁
     * @param key 锁 key
     * @return
     */
    public static Boolean lockAsync(int timeout, String key) {
        Jedis jedis = JedisPoolUtil.getJedis();
        Boolean flag = false;
        try {
            Long time = new Date().getTime();
            String myKey = LOCK_PRE_KEY + key;
            long value = jedis.setnx(myKey, time.toString());
            flag = value > 0;
            if (flag) {
                jedis.expire(myKey, timeout);
            }
        } finally {
            JedisPoolUtil.returnRes(jedis);
        }
        return flag;
    }

    public static Boolean lockAsync(String key) {
        return lockAsync(DEFAULT_TIME_OUT, key);
    }

    /**
     * 解锁
     *
     * @param key
     */
    public static void unLock(String key) {
        Jedis jedis = JedisPoolUtil.getJedis();
        try {
            jedis.del(LOCK_PRE_KEY + key);
        } finally {
            JedisPoolUtil.returnRes(jedis);
        }
    }
}
