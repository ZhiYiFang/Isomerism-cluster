/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.utils;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xidian.synchronizer.syn.utils.rediskey.KeyPrefix;

public class RedisService {

	private JedisPool jedisPool;
	private Gson gson;

	public static RedisService getInstance() {
		String host = "172.17.0.4";
		String port = "6379";
		String timeout = "10";
		String poolMaxTotal = "1000";
		String poolMaxIdle = "500";
		String poolMaxWait = "500";
		return new RedisService(host, port, timeout, poolMaxTotal, poolMaxIdle, poolMaxWait);
	}

	public RedisService(String host, String port, String timeout, String poolMaxTotal, String poolMaxIdle,
			String poolMaxWait) {
		gson = new Gson();
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.parseInt(poolMaxIdle));
		poolConfig.setMaxTotal(Integer.parseInt(poolMaxTotal));
		poolConfig.setMaxWaitMillis(Integer.parseInt(poolMaxWait) * 1000);
		jedisPool = new JedisPool(poolConfig, host, Integer.parseInt(port), Integer.parseInt(timeout) * 1000);
	}

	public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			String str = jedis.get(realKey);
			T t = stringToBean(str, clazz);
			return t;
		} finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 设置对象
	 */
	public <T> boolean set(KeyPrefix prefix, String key, T value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String str = beanToString(value);
			if (str == null || str.length() <= 0) {
				return false;
			}
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			int seconds = prefix.expireSeconds();
			if (seconds <= 0) {
				jedis.set(realKey, str);
			} else {
				jedis.setex(realKey, seconds, str);
			}
			return true;
		} finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 判断key是否存在
	 */
	public <T> boolean exists(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			return jedis.exists(realKey);
		} finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 增加值
	 */
	public <T> Long incr(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			return jedis.incr(realKey);
		} finally {
			returnToPool(jedis);
		}
	}

	/**
	 * 减少值
	 */
	public <T> Long decr(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// 生成真正的key
			String realKey = prefix.getPrefix() + key;
			return jedis.decr(realKey);
		} finally {
			returnToPool(jedis);
		}
	}

	public <T> boolean delete(KeyPrefix prefix, String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			String realKey = prefix.getPrefix() + key;
			return jedis.del(realKey) > 0;
		} finally {
			returnToPool(jedis);
		}
	}

	private <T> String beanToString(T value) {
		if (value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if (clazz == int.class || clazz == Integer.class) {
			return "" + value;
		} else if (clazz == String.class) {
			return (String) value;
		} else if (clazz == long.class || clazz == Long.class) {
			return "" + value;
		} else {
			return gson.toJson(value);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T stringToBean(String str, Class<T> clazz) {
		if (str == null || str.length() <= 0 || clazz == null) {
			return null;
		}
		if (clazz == int.class || clazz == Integer.class) {
			return (T) Integer.valueOf(str);
		} else if (clazz == String.class) {
			return (T) str;
		} else if (clazz == long.class || clazz == Long.class) {
			return (T) Long.valueOf(str);
		} else {
			return gson.fromJson(str, clazz);
		}
	}

	private void returnToPool(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
}
