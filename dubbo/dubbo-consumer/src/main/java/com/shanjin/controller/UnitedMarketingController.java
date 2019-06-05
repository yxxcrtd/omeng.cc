package com.shanjin.controller;

import com.shanjin.common.util.CommonResultVo;
import com.shanjin.common.util.StringUtil;
import com.shanjin.model.PersAssInfo;
import com.shanjin.service.IUnitedMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/5
 * @desc 联合营销 -- 一对一快捷支付
 */
@Controller
@RequestMapping("/unitedMarketing")
public class UnitedMarketingController {


    @Autowired
    private IUnitedMarketingService unitedMarketingService;
    /**
     * 是否加入联合营销营销
     *
     * @param merchantId
     * @return
     */
    @RequestMapping("/getUmInfo")
    @ResponseBody
    public CommonResultVo getUmInfo(@RequestParam("merchantId") Long merchantId) {
        CommonResultVo resultVo = new CommonResultVo();
        if(null == merchantId){
            resultVo.setResultCode("001");
            resultVo.setMessage("参数不能为空");
            return resultVo;
        }
        Map<String,Object> map = unitedMarketingService.getUnitedMarketingInfo(merchantId);
        resultVo.setData(map);
        return resultVo;
    }


    /**
     *查询商户私人助理信息
     * @param merchantId
     * @return
     */
    @RequestMapping("/queryPersInfoForMch")
    @ResponseBody
    public CommonResultVo queryPersInfoForMerchant(@RequestParam("merchantId") Long merchantId){
        CommonResultVo resultVo = new CommonResultVo();
        if(null == merchantId){
            resultVo.setResultCode("001");
            resultVo.setMessage("参数不能为空");
            return resultVo;
        }
        PersAssInfo persAssInfo = unitedMarketingService.queryPersInfoForMerchant(merchantId);
        resultVo.setData(persAssInfo);
        return resultVo;
    }
    /**
     * 绑定商户私人助理
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "/bindMerchant",method = RequestMethod.POST)
    @ResponseBody
    public CommonResultVo bindMerchantPersAss(@RequestBody Map<String,Object> paramMap) {
        //@RequestParam("merchantId") Long merchantId, @RequestParam("inviteCode") String inviteCode
        String inviteCode = (String) paramMap.get("inviteCode");
        Long merchantId= (Long)paramMap.get("merchantId");
        CommonResultVo resultVo = new CommonResultVo();
        if(null == merchantId || StringUtil.isEmpty(inviteCode)){
            resultVo.setResultCode("001");
            resultVo.setMessage("参数不能为空");
            return resultVo;
        }
        PersAssInfo persAssInfo = unitedMarketingService.bindMerchantPersAss(merchantId,inviteCode);
        if(null == persAssInfo){
            resultVo.setResultCode("002");
            resultVo.setMessage("绑定失败");
        }else {
            resultVo.setData(persAssInfo);
        }
        return resultVo;
    }

    /**
     * 加入联合营销  过期
     * @return
     */
    @Deprecated
    //@RequestMapping(value = "/joinUmPlan",method = RequestMethod.POST)
    @ResponseBody
    public CommonResultVo joinUmPlan(@RequestParam("merchantId") Long merchantId){
        return null;
    }

    /**
     * 查询商户分成列表
     * @param merchantId
     * @return
     */
    @RequestMapping("/findBainList")
    @ResponseBody
    public CommonResultVo findBainList(@RequestParam("merchantId") Long merchantId,@RequestParam("pageNum")int pageNum,@RequestParam("pageSize") int pageSize){

        CommonResultVo resultVo = new CommonResultVo();
        if(null == merchantId){
            resultVo.setResultCode("001");
            resultVo.setMessage("参数不能为空");
            return resultVo;
        }
        if(pageNum <0){
            pageNum = 0;
        }
        if(pageSize <=0){
            pageSize = 20;
        }
        List<Map<String,Object>> list = unitedMarketingService.findBainList(merchantId,pageNum,pageSize);
        resultVo.setData(list);
        return resultVo;
    }


}
