package com.shanjin.controller;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.CommonResultVo;
import com.shanjin.common.util.StringUtil;
import com.shanjin.model.PersAssInfo;
import com.shanjin.service.ICplanKingService;
import com.shanjin.service.IKingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 王牌计划
 *
 * @author Administrator
 */
@Controller
@RequestMapping("/king")
public class KingController {
    // 本地失败日志记录对象
    private static final Logger logger = Logger.getLogger(KingController.class);

    @Autowired
    private IKingService kingService;

    @Autowired
    private ICplanKingService cplanKingService;

    /**
     * @param userId
     * @return
     */
    @RequestMapping("/queryUserAssetAmount")
    @SystemControllerLog(description = "月可用消费金余额")
    public
    @ResponseBody
    Object queryUserAssetAmount(Long userId) {
        JSONObject jsonObject = null;
        try {

            jsonObject = this.kingService.queryUserAssetAmount(userId);
        } catch (Exception e) {
            MsgTools.sendMsgOrIgnore(e, "queryUserAssetAmount");
            jsonObject = new ResultJSONObject("queryUserAssetAmount_exception", "月可用消费金余额失败");
            logger.error("月可用消费金余额失败", e);
        }
        return jsonObject;
    }

    /**
     * 用户消费金明细
     *
     * @param userId
     * @return
     */
    @RequestMapping("/queryUserAssetRecorders")
    @SystemControllerLog(description = "用户消费金明细")
    public
    @ResponseBody
    Object queryUserAssetRecorders(Long userId,Integer pageSize, Integer pageNo) {
        JSONObject jsonObject = null;
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userId", userId);
            param.put("pageSize", pageSize);
            param.put("pageNo", pageNo);
            jsonObject = this.kingService.queryKingUserAssetRecorderList(param);
        } catch (Exception e) {
            MsgTools.sendMsgOrIgnore(e, "queryKingUserAssetInfo");
            jsonObject = new ResultJSONObject("queryKingUserAssetInfo_exception", "用户消费金明细失败");
            logger.error("用户消费金明细失败", e);
        }
        return jsonObject;
    }

    /**
     * H5 接口
     *
     * @return
     * @Param
     */
    @RequestMapping("/getPkgList")
    @ResponseBody
    public CommonResultVo quertKingPkgList() {
        //获取启用的
        List<Map<String, Object>> list = kingService.getConsumePkgList();
        CommonResultVo vo = new CommonResultVo();
        vo.setData(list);
        return vo;
    }

    /**
     * H5接口 获取用户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public CommonResultVo getUserInfo(@RequestParam("userId") Long userId) {
        Map<String, Object> userInfoMap = null;
        if (null != userId) {
            userInfoMap = kingService.getUserInfo(userId);
        }
        CommonResultVo vo = new CommonResultVo();
        vo.setData(userInfoMap);
        return vo;
    }

    @RequestMapping("/validInviteCode")
    @ResponseBody
    public CommonResultVo validInviteCode(@RequestParam("inviteCode") String code) {
        CommonResultVo vo = new CommonResultVo();
        if (StringUtil.isEmpty(cplanKingService)) {
            vo.setResultCode("001");
            vo.setMessage("服务码不能为空");
            return vo;
        }
        try {
            PersAssInfo persAssInfo = cplanKingService.validatePersAssInviteCode(code);
            if (null == persAssInfo) {
                vo.setResultCode("002");
                vo.setMessage("服务码错误或者不存在");
                return vo;
            }
            vo.setData(persAssInfo);
            return vo;
        } catch (Exception e) {
            logger.error("邀请码验证接口异常", e);
            vo.setResultCode("003");
            vo.setMessage("服务码接口异常");
            return vo;
        }
    }

    /**
     * 王牌会员私人助理信息及消费金信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/queryKingUserAssetInfo")
    @SystemControllerLog(description = "王牌会员私人助理信息及消费金信息")
    public
    @ResponseBody
    Object queryKingUserAssetInfo(Long userId) {

        JSONObject jsonObject = null;
        try {
            jsonObject = this.kingService.queryKingUserAssetInfo(userId);
        } catch (Exception e) {
            MsgTools.sendMsgOrIgnore(e, "queryKingUserAssetInfo");
            jsonObject = new ResultJSONObject("queryKingUserAssetInfo_exception", "王牌会员私人助理信息及消费金信息失败");
            logger.error("王牌会员私人助理信息及消费金信息失败", e);
        }
        return jsonObject;
    }


    /**
     * 获取王牌会员状态信息 app
     *
     * @return
     */
    @RequestMapping("/getKingMemberStatus")
    @ResponseBody
    public Map<String, String> getKingMemberStatus(@RequestParam("userId") Long userId) {
        Map<String, String> rstMap = new HashMap<String, String>();

        if (null == userId) {
            rstMap.put("resultCode", "001");
            rstMap.put("message", "userId不能为空");
        }

        Map<String, String> map = kingService.getKingLoginStatus(userId);
        rstMap.putAll(map);
        rstMap.put("resultCode", "000");
        return rstMap;
    }

    /**
     * APP接口
     *
     * @param pkgId
     * @return
     */
    @RequestMapping("/getPkgDetail")
    @ResponseBody
    public Map<String, Object> getPkgDetail(@RequestParam("pkgId") Long pkgId) {
        Map<String, Object> rstMap = new HashMap<String, Object>();
        if (null == pkgId) {
            rstMap.put("resultCode", "001");
            rstMap.put("message", "pkgId 不能为空");
            return rstMap;
        }

        Map<String, Object> map = kingService.getConsumePkgDetail(pkgId);
        if (null == map || map.isEmpty()) {
            rstMap.put("resultCode", "002");
            rstMap.put("message", "找不到指定的服务包");
            return rstMap;
        }
        BigDecimal price = (BigDecimal) map.get("price");
        int typeValue = 20;//王牌会员
        String name = "王牌会员计划";
        rstMap.put("resultCode", "000");
        rstMap.put("charge", price);
        rstMap.put("typeValue", typeValue);
        rstMap.put("name", name);

        return rstMap;
    }

    /**
     * 王牌计划购买成功展示王牌会员私人助理信息及消费金信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/queryKingInfo")
    @SystemControllerLog(description = "王牌计划购买成功展示王牌会员私人助理信息及消费金信息")
    public
    @ResponseBody
    Object queryKingInfo(Long userId) {

        JSONObject jsonObject = null;
        try {
            jsonObject = this.kingService.queryKingInfo(userId);
        } catch (Exception e) {
            MsgTools.sendMsgOrIgnore(e, "queryKingInfo");
            jsonObject = new ResultJSONObject("queryKingInfo_exception", "王牌计划购买成功展示王牌会员私人助理信息及消费金信息失败");
            logger.error("王牌计划购买成功展示王牌会员私人助理信息及消费金信息失败", e);
        }
        return jsonObject;
    }


    /**
     * 活动分享
     * @return
     */
    @RequestMapping("/getActShare")
    @ResponseBody
    public Map<String,Object>  actShare(@RequestParam("activityId") Long actId){
        if(actId == null){
            return new  ResultJSONObject("001","参数不能为空");
        }
        Map<String,Object> rstMap = kingService.shareAct(actId);
        if(null == rstMap || rstMap.isEmpty()){
            rstMap = new HashMap<String,Object>(2);
            rstMap.put("resultCode","002");
            rstMap.put("message","找不到指定活动分享信息");
            return rstMap;
        }
        rstMap.put("resultCode","000");
        return rstMap;
    }

    /**
     * H5 接口
     *
     * @return
     * @Param
     */
    @RequestMapping("/queryKingPkg")
    @ResponseBody
    public CommonResultVo quertMaxKingPkg() {
        CommonResultVo resultVo = new CommonResultVo();
        //获取启用的
        List<Map<String, Object>> list = kingService.getConsumePkgList();
        BigDecimal bigDecimal = new BigDecimal("1080");
        for(Map<String, Object> map : list){
            BigDecimal amount = new BigDecimal(map.get("price").toString());
            if(amount.compareTo(bigDecimal) == 0){
                resultVo.setData(map);
                return resultVo;
            }
        }
        resultVo.setResultCode("001");
        resultVo.setMessage("找不到指定的服务包");
        return resultVo;
    }


    /**
     * 查询用户王牌会员信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/queryUserKingMemberInfo")
    @SystemControllerLog(description = "查询用户王牌会员信息")
    public
    @ResponseBody
    Object queryUserKingMemberInfo(Long userId) {

        JSONObject jsonObject = null;
        try {
            jsonObject = this.kingService.queryUserKingMemberInfo(userId);
        } catch (Exception e) {
            MsgTools.sendMsgOrIgnore(e, "queryUserKingMemberInfo");
            jsonObject = new ResultJSONObject("queryUserKingMemberInfo_exception", "查询用户王牌会员信息失败");
            logger.error("查询用户王牌会员信息失败", e);
        }
        return jsonObject;
    }

    /**
     * 快捷支付h5登出
     *
     * @param userId
     * @return
     */
    @RequestMapping("/h5Exit")
    @SystemControllerLog(description = "快捷支付h5登出")
    public
    @ResponseBody
    Object h5Exit(String openId,Long userId) {

        JSONObject jsonObject = null;
        try {
            jsonObject = this.kingService.h5Exit(openId,userId);
        } catch (Exception e) {
            MsgTools.sendMsgOrIgnore(e, "h5Exit");
            jsonObject = new ResultJSONObject("h5Exit_exception", "快捷支付h5登出失败");
            logger.error("快捷支付h5登出失败", e);
        }
        return jsonObject;
    }

}
