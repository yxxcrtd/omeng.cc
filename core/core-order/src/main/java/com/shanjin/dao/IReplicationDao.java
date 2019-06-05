package com.shanjin.dao;

import java.util.Map;

/**
 * 同步复制的DAO 
 * @author Revoke 2016.6.13
 *
 */
public interface IReplicationDao {
	
		//增加一条待从库执行的SQL。
	    public void addReplicateSql(Map<String,String> param);
}
