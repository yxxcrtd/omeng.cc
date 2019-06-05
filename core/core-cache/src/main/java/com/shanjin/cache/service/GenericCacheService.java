package com.shanjin.cache.service;

import com.shanjin.cache.service.impl.JedisLock;




public interface GenericCacheService {

	/** set Object */
	public String set(String key,Object object);

	/** get Object */
	public Object get(String key);

	/** delete a key **/
	public boolean del(String key);
	
	/** delete contains key **/
	public boolean delContainsKey(String key,boolean flag);
}
