package com.shanjin.incr.util;

import com.shanjin.incr.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/18
 * @desc Bean 工具类  model->map转换
 * @see
 */
public class BeanUtil {
    private  static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static Map<String,String> bean2StrMap(Object obj){
        Map<String,Object> map = bean2ObjMap(obj);
        Map<String,String> strMap = new HashMap<>();
        for(Map.Entry<String,Object> entry : map.entrySet()){
            strMap.put(entry.getKey(),entry.getValue().toString());
        }
        return strMap;
    }


    public static Map<String,Object> bean2ObjMap(Object bean){
        Map<String, Object> returnMap = null;
        try {
            returnMap = new HashMap<>();
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i< propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("bean2ObjMap 异常,bean:{}",bean);
        }
        return returnMap;
    }


    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(12120L);
        userInfo.setName("hurd");
        userInfo.setJoinTime(new Date());
        userInfo.setIsDel(1);
        userInfo.setIsVerification(1);

        Map<String,String> map= bean2StrMap(userInfo);
        logger.info("打印的日志:{}",map);
    }
}
