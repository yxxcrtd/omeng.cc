package com.shanjin.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/5
 * @desc 用户相关信息封存 线程安全  便于获取相关信息 token认证的接口才会存在 相关信息
 */
public class UserInfo {

    private ThreadLocal<Map<USERINFO_KEY, ? extends Serializable>> mapThreadLocal = new InheritableThreadLocal<>();

    public <T extends Serializable> T get(USERINFO_KEY key, Class<T> clazz) {
        Map<USERINFO_KEY, ?> map = mapThreadLocal.get();
        return (T) map.get(key);
    }

    public <T extends Serializable> void set(USERINFO_KEY key, T t) {
        Map<USERINFO_KEY, Serializable> map = (Map<USERINFO_KEY, Serializable>) mapThreadLocal.get();
        map.put(key, t);
        mapThreadLocal.set(map);
    }


    public enum USERINFO_KEY {
        USER_ID, CLIENT_ID, PHONE,TOKEN;
    }

    private UserInfo() {
        Map<USERINFO_KEY, ? extends Serializable> map = new HashMap<>();
        mapThreadLocal.set(map);
    }

    public static final UserInfo getInstance() {
        return UserInfoHolder.INSTANCE;
    }


    private static class UserInfoHolder {
        private final static UserInfo INSTANCE = new UserInfo();
    }
}
