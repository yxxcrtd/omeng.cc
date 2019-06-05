package com.shanjin.carinsur.service.impl;

import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.carinsur.dao.ActConfigInfoMapper;
import com.shanjin.carinsur.dao.CiUserVoucherMapper;
import com.shanjin.carinsur.model.CiUserVoucher;
import com.shanjin.carinsur.service.ConfigService;
import com.shanjin.carinsur.service.VoucherService;
import com.shanjin.carinsur.util.DateUtil;
import com.shanjin.carinsur.util.UserVouStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.shanjin.common.util.DateUtil.DATE_TIME_PATTERN;
import static com.shanjin.common.util.StringUtil.*;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/3
 * @desc 车险券服务
 */
@Service("voucherService")
public class VoucherServiceImpl implements VoucherService {

    private static final Logger logger = LoggerFactory.getLogger(VoucherServiceImpl.class);

    @Autowired
    private CiUserVoucherMapper userVoucherMapper;
    @Autowired
    private ActConfigInfoMapper configInfoMapper;

    @Autowired
    private ICommonCacheService commonCacheService;
    @Autowired
    private ConfigService configService;

    private static final String SHOW_CAR_INSUR_BANNER = "show_car_insur_banner";

    private static final String SHOW_BANNER_VALUE = "true";

    private static final String CACHE_USER_HASH_VOUCHER_KEY = "third_service_hash_voucher:userId";



    @Override
    public List<CiUserVoucher> findVounchrByParams(Long userId, Integer vouType, Integer status) {


        CiUserVoucher voucher = new CiUserVoucher();
        voucher.setUserId(userId);
        voucher.setVouType(vouType);
        voucher.setStatus(status);
        voucher.setCurTime(DateUtil.date2YmdDate(new Date()));

        return userVoucherMapper.findVounchrByParams(voucher);
    }

    @Override
    public int updateExpireUserVouchers() {

        Date date = DateUtil.date2YmdDate(new Date());
        return userVoucherMapper.batchUpdateExpireVouchers(date, UserVouStatusEnum.HAS_EXPIRED.getValue(), UserVouStatusEnum.NOT_USED.getValue());
    }

    public int saveUserVoucher(Map<String, Object> params) throws Exception {
        CiUserVoucher userVoucher = new CiUserVoucher();
        userVoucher.setUserId(nullToLong(params.get("userId")));
        userVoucher.setPhone(null2Str(params.get("phone")));
        userVoucher.setOriginDesc(null2Str(params.get("originDesc")));
        userVoucher.setOriginId(nullToLong(params.get("originId")));
        userVoucher.setOrginId2(null2Str(params.get("originId2")));
        userVoucher.setOrginId3(null2Str(params.get("originId3")));
        userVoucher.setOriginType(nullToInteger(params.get("originType")));
        userVoucher.setStatus(nullToInteger(params.get("status")));
        userVoucher.setStartTime(com.shanjin.common.util.DateUtil.convertStringToDate(null2Str(params.get("startTime"))));
        userVoucher.setEndTime(com.shanjin.common.util.DateUtil.convertStringToDate(null2Str(params.get("endTime"))));
        userVoucher.setCreateTime(com.shanjin.common.util.DateUtil.convertStringToDate(DATE_TIME_PATTERN, null2Str(params.get("createTime"))));
        userVoucher.setVouCode(null2Str(params.get("vouCode")));
        userVoucher.setVouNo(null2Str(params.get("vouNo")));
        userVoucher.setIsDel(nullToInteger(params.get("isDel")));
        return userVoucherMapper.saveEntity(userVoucher);
    }

    @Override
    public boolean isShowCarInsurBanner(Long userId) {
        String value = configService.getCfgValue(SHOW_CAR_INSUR_BANNER);
        boolean flag = SHOW_BANNER_VALUE.equalsIgnoreCase(value);
        if (flag) {
            String cache = commonCacheService.hget(CACHE_USER_HASH_VOUCHER_KEY, "" + userId);
            if (StringUtils.isEmpty(cache)) {
                CiUserVoucher voucher = new CiUserVoucher();
                voucher.setUserId(userId);
                int count = userVoucherMapper.getCount(voucher);
                flag = count > 0;
                //cache
                if (flag) {
                    commonCacheService.hset(CACHE_USER_HASH_VOUCHER_KEY, "" + userId, "1");
                }
            }
        }
        return flag;
    }
}
