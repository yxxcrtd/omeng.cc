package com.shanjin.common.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import redis.clients.jedis.Jedis;

import com.shanjin.cache.util.JedisPoolUtil;

public class createIndexListener implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = Logger.getLogger(createIndexListener.class);

	// 启动时创建索引
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			Jedis jedis = JedisPoolUtil.getJedis();
			try {
				// JedisLock lock = new JedisLock(jedis,
				// CacheConstants.LOCK_CREATE_INDEX_CACHE, 2 * 60 * 1000, 2 * 60
				// * 1000);
				// if (lock.acquire()) { // 获得创建索引缓存锁的线程，才能执行工作。
				// System.out.println(DateUtil.getNowYYYYMMDDHHMMSS() +
				// "====启动创建关键词索引====");
				// System.out.println(DateUtil.getNowYYYYMMDDHHMMSS() +
				// "====启动创建个性服务索引====");
				// System.out.println(DateUtil.getNowYYYYMMDDHHMMSS() +
				// "====启动创建索引结束====");
				// }
			} catch (Exception e) {
				logger.error("创建索引出错：" + e.getMessage(), e);
			} finally {
				JedisPoolUtil.returnRes(jedis);
			}
		}

	}

}
