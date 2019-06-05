package com.shanjin.service.impl;

import com.shanjin.common.util.MD5Util;
import com.shanjin.dao.UserRelateMapper;
import com.shanjin.service.ISilenceLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc 静默登录接口
 */
@Service("silenceLoginServiceImpl")
public class SilenceLoginServiceImpl implements ISilenceLoginService {


    @Autowired
    private UserRelateMapper userRelateMapper;

    @Override
    public Map<String, Object> getSilenceLoginParams(String openId) {

        Map<String, Object> map = userRelateMapper.getUserInfoByOpenId(openId);
        if (map != null && !map.isEmpty()) {
            String phone = map.get("phone").toString();
            Long userId = Long.valueOf(map.get("userId").toString());
            String openKey = MD5Util.MD5_32(phone + "#" + userId);
            map.put("openKey", openKey);
        }
        return map;
    }
}
