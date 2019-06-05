package com.shanjin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.ServletUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.model.PersAssInfo;
import com.shanjin.model.ResultVo;
import com.shanjin.service.CplanSerive;
import com.shanjin.service.IUserCPlanService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/cplan")
public class CPlanController {
    @Reference
    private IUserCPlanService userCPlanService;
    @Autowired
    private CplanSerive cplanSerive;

    private static final Logger logger = Logger.getLogger(CPlanController.class);


    /**
     * c plan注册用户
     */
    @RequestMapping("/user/register")
    @SystemControllerLog(description = "c plan注册用户")
    public
    @ResponseBody
    Object registerFromCPlan(String phone) {
        String ip = IPutil.getIpAddr(ServletUtil.getRequest());
        JSONObject jsonObject = null;
        try {
            jsonObject = this.userCPlanService.registerFromCPlan(phone, ip);
        } catch (Exception e) {
            MsgTools.sendMsgOrIgnore(e, "registerFromCPlan");

            jsonObject = new ResultJSONObject("registerFromCPlan_exception", "c plan注册用户失败");
            logger.error("c plan注册用户失败", e);
        }
        return jsonObject;
    }


    /**
     * 私人助理开店
     *
     * @param persAssInfo
     * @return
     */
    @RequestMapping(value = "/open_merchant",method = RequestMethod.POST)
    @ResponseBody
    public ResultVo<Map<String, Object>> openMerchantForPersAssistant(@RequestBody PersAssInfo persAssInfo) {

        ResultVo<Map<String, Object>> resultVo = new ResultVo();
        //参数校验 --不需要正则匹配么？
        String phone = persAssInfo.getPhone();
        Pattern pattern = Pattern.compile("^(1)\\d{10}$");
        Matcher matcher = pattern.matcher(phone);
        if (StringUtil.isEmpty(phone) || !matcher.matches()) {
            resultVo.setRetcode("001");
            resultVo.setRetmsg("系统调用失败，输入的手机号不合法");

        } else {
            try {
                Map<String, Object> map = cplanSerive.openShopForPersAss(persAssInfo);
                resultVo.setRetcode("000");
                resultVo.setRetmsg("开店调用成功");
                resultVo.setRetdata(map);
            } catch (Exception e) {
                logger.warn(e);
                resultVo.setRetcode("002");
                resultVo.setRetmsg(e.getMessage());
            }
        }
        return resultVo;
    }


    /**
     * 私人助理关店...
     *
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "/close_merchant",method = RequestMethod.POST)
    @ResponseBody
    public ResultVo<Object> closeMerchantForPersAssistant(@RequestBody Map<String, String> paramMap) {
        ResultVo<Object> resultVo = new ResultVo<Object>();

        Long userId = null;
        Long merchantId = null;
        try {
            userId = Long.valueOf(paramMap.get("userId"));
            merchantId = Long.valueOf(paramMap.get("merchantId"));
        } catch (NumberFormatException e) {
            resultVo.setRetcode("001");
            resultVo.setRetmsg("传入的参数非法");
            return resultVo;
        }

        if (null == userId && null == merchantId) {
            resultVo.setRetcode("001");
            resultVo.setRetmsg("参数不能为空");
        } else {
            try {
                cplanSerive.closeShopForPersAss(userId, merchantId);
                resultVo.setRetcode("000");
                resultVo.setRetmsg("关店成功");
            } catch (Exception e) {
                logger.warn(e);
                resultVo.setRetcode("001");
                resultVo.setRetmsg("系统异常，msg:" + e.getMessage());
            }
        }
        return resultVo;
    }

}
