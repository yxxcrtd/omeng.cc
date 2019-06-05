package com.shanjin.controller;

import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.util.CommonResultVo;
import com.shanjin.common.util.IPutil;
import com.shanjin.goldplan.model.InvitedRecord;
import com.shanjin.goldplan.service.InviteService;
import com.shanjin.util.ActivityException;
import com.shanjin.util.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/9/19
 * @desc 摸金计划 邀请controller
 */
@Controller
@RequestMapping("/invite")
public class InviteController {


    @Autowired
    private InviteService inviteService;

    private static final Logger logger = LoggerFactory.getLogger(InviteController.class);

    /**
     * 获取邀请人用户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/getInviteUserInfo")
    @ResponseBody
    public CommonResultVo getInviteUserInfo(@RequestParam("userId") Long userId) {

        if (null == userId) {
            return new CommonResultVo("001", "参数不能为空");
        }
        try {
            Map<String, String> map = inviteService.getInviteUserInfo(userId);
            CommonResultVo resultVo = new CommonResultVo();
            resultVo.setData(map);
            return resultVo;
        } catch (ActivityException e) {
            logger.info("获取邀请用户信息异常", e);
            return new CommonResultVo(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            logger.info("获取邀请用户信息异常", e);
            return new CommonResultVo("001", e.getMessage());
        }
    }

    @RequestMapping(value = "/addInvitedRecord",method = RequestMethod.POST)
    @ResponseBody
    public CommonResultVo addInvitedRecord(@RequestBody InvitedRecord invitedRecord , HttpServletRequest request) {


        if (null == invitedRecord.getUserId() || null == invitedRecord.getActivityId() ||
                StringUtils.isEmpty(invitedRecord.getUserPhone()) || StringUtils.isEmpty(invitedRecord.getInvitedPhone())) {
            return new CommonResultVo("001", "参数不能为空");
        }
        if (!RegexUtil.isPhoneNo(invitedRecord.getUserPhone()) || !RegexUtil.isPhoneNo(invitedRecord.getInvitedPhone())) {
            return new CommonResultVo("001", "手机号异常");
        }
        invitedRecord.setInvitedIp(IPutil.getIpAddr(request));

        try {
            int count = inviteService.addInvitedRecord(invitedRecord);
            CommonResultVo resultVo = new CommonResultVo();
            Map<String,Integer> map = new HashMap<>(1);
            map.put("type",count);
            resultVo.setData(map);
            return resultVo;
        } catch (ActivityException e) {
            logger.info("添加邀请记录异常", e);
            return new CommonResultVo(e.getErrorCode(), e.getMessage());

        } catch (Exception e) {
            logger.info("添加邀请记录异常", e);
            return new CommonResultVo("001", e.getMessage());
        }
    }
    

	@RequestMapping("/findTouchGoldChance")
	@SystemControllerLog(description = "查询用户摸金机会")
	public @ResponseBody Object findTouchGoldChance(Long userId,Long activityId) {
        if (null == userId ) {
            return new CommonResultVo("001", "参数不能为空");
        }
        try {
            Map<String,Object> value = inviteService.findTouchGoldChance(userId,activityId);
            CommonResultVo resultVo = new CommonResultVo();
            resultVo.setData(value);
            return resultVo;
        } catch (ActivityException e) {
            logger.info("查询摸金机会异常", e);
            return new CommonResultVo(e.getErrorCode(), e.getMessage());

        } catch (Exception e) {
            logger.info("查询摸金机会异常", e);
            return new CommonResultVo("001", e.getMessage());
        }
    }
    

	@RequestMapping("/touchGold")
	@SystemControllerLog(description = "用户摸金")
	public @ResponseBody Object touchGold(Long userId,Long activityId,int source) {
//		source 1-订单，2-好友
        if (null == userId) {
            return new CommonResultVo("001", "参数不能为空");
        }
        try {
            Map<String,Object> value = inviteService.touchGold(userId,activityId,source);
            CommonResultVo resultVo = new CommonResultVo();
            resultVo.setData(value);
            return resultVo;
        } catch (ActivityException e) {
            logger.info("摸金异常", e);
            return new CommonResultVo(e.getErrorCode(), e.getMessage());

        } catch (Exception e) {
            logger.info("摸金异常", e);
            return new CommonResultVo("001", e.getMessage());
        }
    }
}
