package com.shanjin.carinsur.service;

import com.shanjin.carinsur.model.CiUserVoucher;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/3
 * @desc 车险券服务
 */
public interface VoucherService {

    /**
     * 查询用户指定券类型指定状态的券列表信息
     * @param userId
     * @param vouType
     * @param status
     * @return
     */
    List<CiUserVoucher> findVounchrByParams(Long userId, Integer vouType, Integer status);

    /**
     * 更新用户过期券状态 0--> -1
     * @return 返回更新条数
     */
    int updateExpireUserVouchers();

    int saveUserVoucher(Map<String, Object> params) throws Exception;


    /**
     * 是否显示车险banner
     * @param userId
     * @return
     */
    boolean isShowCarInsurBanner(Long userId);

}
