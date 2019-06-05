package com.shanjin.interceptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.annotation.Resources;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;




import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.ReflectHelper;
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class })})
public class BulkSqlBatisInterceptor  implements Interceptor {	
	private static  Map<String,String> replicateMap= new HashMap<String,String>();
	private static  String sqlStatement="INSERT INTO replicate_trace(addTime, tableName,content)  values (now(),?,?)";
	
	static {
			replicateMap.put("com.shanjin.dao.IPushDao.insertPushMerchantOrder", "push_merchant_order");
			replicateMap.put("com.shanjin.dao.IOrderInfoDao.updateMerchantOrderStatus", "push_merchant_order");
			replicateMap.put("com.shanjin.dao.IOrderInfoDao.updateOtherMerchantOrderStatus", "push_merchant_order");
			replicateMap.put("com.shanjin.dao.IOrderInfoDao.batchUpdateMerchantOrderStatus", "push_merchant_order");
	}
	

    public Object intercept(Invocation invocation) throws Throwable {
    	if (invocation.getTarget() instanceof RoutingStatementHandler) {
    		RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
    		BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler, "delegate");
    		BoundSql boundSql = delegate.getBoundSql();
    		MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");
    		Connection connection = (Connection) invocation.getArgs()[0];  
    		String sqlId = mappedStatement.getId();
    		
    		if (replicateMap.containsKey(sqlId) ) {
    			String preapreSql = boundSql.getSql();
    			Configuration configuration = mappedStatement.getConfiguration();
    			String binderSql = showSql(configuration,boundSql); 
    			addSqlToReplication((Connection)invocation.getArgs()[0],binderSql,sqlId);
    		}
    	}
    	
    	return invocation.proceed();
    }
 
    //将SQL插入同步复制的表中
    private void addSqlToReplication(Connection conn,String sql, String sqlId) throws Exception {
    	    try{
    			PreparedStatement  pstmt =conn.prepareStatement(sqlStatement);
    			pstmt.setString(1,replicateMap.get(sqlId) );
    			pstmt.setString(2, sql);
    			pstmt.execute();
    		 }catch(Exception e){
    			 throw e;
    		 }
	}
 
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format((Date) obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            } 
        }
        return value;
    }
 
    private static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
 
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else {
                    	 	//null found
                    	sql = sql.replaceFirst("\\?", "null");
                    }
                }
            }
        }
        return sql;
    }
 
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

	@Override
	public void setProperties(Properties arg0) {
		
	}
 
}
