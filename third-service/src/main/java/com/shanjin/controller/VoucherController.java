package com.shanjin.controller;

import com.shanjin.carinsur.model.CiUserVoucher;
import com.shanjin.carinsur.service.VoucherService;
import com.shanjin.common.util.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/2
 * @desc 代金券业务
 */
@Controller
@RequestMapping("/voucher")
public class VoucherController {


    @Autowired
    private VoucherService voucherService;

    @ResponseBody
    @RequestMapping("/validate/getVoucherList")
    public CommonResultVo<?> getVoucherList(@RequestParam("userId") Long userId,@RequestParam("vouType") Integer vouType,@RequestParam("status") Integer status){
        if(null == userId || null == vouType || null == status){
            return new CommonResultVo("001","指定参数不能为");
        }
        List<CiUserVoucher> list = voucherService.findVounchrByParams(userId,vouType,status);
        CommonResultVo<List<CiUserVoucher>> commonResultVo = new CommonResultVo<>();
        commonResultVo.setData(list);

        return commonResultVo;
    }

    @ResponseBody
    @RequestMapping("/validate/isShowCarInsurBanner")
    public CommonResultVo<Boolean> isShowCarInsurBanner(@RequestParam("userId") Long userId){
        if(null == userId){
            return new CommonResultVo<>("001","参数不能为空");
        }
        Boolean flag = voucherService.isShowCarInsurBanner(userId);
        CommonResultVo commonResultVo = new CommonResultVo("000");
        commonResultVo.setData(flag);
        return commonResultVo;
    }
}
