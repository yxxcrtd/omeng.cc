package com.shanjin.service;


import com.shanjin.model.RuleConfig;

import java.util.List;

/**
 * Created by hurd on 2016/8/12.
 * 增值服务接口
 */
public interface IncService {


    /**
     * 查找商户规则配置信息
     * @param merchantIds 商户 id 数组
     * @return 返回基于merchantId对应的RuleConfig列表
     */
    List<RuleConfig> getRuleConfig(Long... merchantIds);

}
