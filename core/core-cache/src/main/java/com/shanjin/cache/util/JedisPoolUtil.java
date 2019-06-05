package com.shanjin.cache.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.shanjin.common.util.StringUtil;

public class JedisPoolUtil {
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(JedisPoolUtil.class);
	// Redis服务器IP
	// private static String ADDR = "192.168.1.48";

	// Redis的端口号
	// private static int PORT = 6379;

	// 可用连接实例的最大数目，默认值为8；
	// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	// private static int MAX_ACTIVE = 1024;

	// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	// private static int MAX_IDLE = 200;

	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	// private static int MAX_WAIT = 10000;

	// private static int TIMEOUT = 10000;

	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	// private static boolean TEST_ON_BORROW = true;

	private static JedisPool pool = null;

	public static Properties getJedisProperties() {

		Properties config = new Properties();
		InputStream is = null;
		try {
			is = JedisPoolUtil.class.getClassLoader().getResourceAsStream("cacheConfig.properties");
			config.load(is);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		return config;
	}

	/**
	 * 创建连接池
	 * 
	 */
	private static void createJedisPool() {
		// 建立连接池配置参数
		JedisPoolConfig config = new JedisPoolConfig();
		Properties prop = getJedisProperties();
		// 设置最大连接数
		config.setMaxActive(StringUtil.nullToInteger(prop.getProperty("MAX_ACTIVE")));
		// 设置最大阻塞时间，记住是毫秒数milliseconds
		config.setMaxWait(StringUtil.nullToInteger(prop.getProperty("MAX_WAIT")));
		// 设置空间连接
		config.setMaxIdle(StringUtil.nullToInteger(prop.getProperty("MAX_IDLE")));
		// jedis实例是否可用
		boolean borrow = prop.getProperty("TEST_ON_BORROW") == "false" ? false : true;
		config.setTestOnBorrow(borrow);
		// 创建连接池
		pool = new JedisPool(config, prop.getProperty("ADDR"), StringUtil.nullToInteger(prop.getProperty("PORT")), StringUtil.nullToInteger(prop.getProperty("TIMEOUT")));// 线程数量限制，IP地址，端口，超时时间
	}

	/**
	 * 在多线程环境同步初始化
	 */
	private static synchronized void poolInit() {
		if (pool == null)
			createJedisPool();
	}

	/**
	 * 获取一个jedis 对象
	 * 
	 * @return
	 */
	public static Jedis getJedis() {
		if (pool == null)
			poolInit();
		return pool.getResource();
	}

	/**
	 * 释放一个连接
	 * 
	 * @param jedis
	 */
	public static void returnRes(Jedis jedis) {
		pool.returnResource(jedis);
	}

	/**
	 * 销毁一个连接
	 * 
	 * @param jedis
	 */
	public static void returnBrokenRes(Jedis jedis) {
		pool.returnBrokenResource(jedis);
	}
	
	
	public static void main(String[] args){
		Jedis jedis=getJedis();
		
	}

}
