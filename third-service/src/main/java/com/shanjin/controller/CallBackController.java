package com.shanjin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.bean.ResultVo;
import com.shanjin.carinsur.model.InsureBaseInfo;
import com.shanjin.carinsur.model.MsgCallBackRecord;
import com.shanjin.carinsur.service.CarInsurService;
import com.shanjin.carinsur.service.ConfigService;
import com.shanjin.carinsur.service.MsgCallBackService;
import com.shanjin.common.util.CommonResultVo;
import com.shanjin.util.CallBackHeaderEnum;
import com.shanjin.util.LogUtil;
import com.shanjin.util.RequestTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc 车险回调接口
 */
@Controller
@RequestMapping("/yangguang")
public class CallBackController {

    private static final Logger logger = LoggerFactory.getLogger(CallBackController.class);

    private static final String URL_CFG_KEY = "yangguang_h5_url";

    @Autowired
    private CarInsurService carInsurService;
    @Autowired
    private MsgCallBackService msgCallBackService;
    @Autowired
    private ConfigService configService;

    private static final String ERROR_PAGE = "/web/activity/cooperation/sinosigError.html?message=";

    @ResponseBody
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public JSONObject notifyCallBack(@RequestBody byte[] bodyByte, @RequestHeader HttpHeaders headers, HttpServletResponse response) throws UnsupportedEncodingException {

        String body = new String(bodyByte, Charset.forName("GBK"));

        LogUtil.logCallBackLog(body);//特定文件日志记录

        JSONObject json = JSON.parseObject(body);
        JSONObject header = json.getJSONObject("Header");
        //header 头校验
        String requestType = header.getString(CallBackHeaderEnum.REQUESTTYPE.getHeadName());
        final String orderNo = header.getString(CallBackHeaderEnum.PARTNERORDERNO.getHeadName());
        String sellerId = header.getString(CallBackHeaderEnum.SELLERID.getHeadName());
        String sendTime = header.getString(CallBackHeaderEnum.SENDTIME.getHeadName());
        String agentCode = header.getString(CallBackHeaderEnum.AGENTCODE.getHeadName());
        String version = header.getString(CallBackHeaderEnum.VERSION.getHeadName());

        //业务处理
        final JSONObject headerJson = genHeaderJson(version, requestType, sellerId, orderNo);
        //callBackRecord封装
        final MsgCallBackRecord callBackRecord;
        try {
            MsgCallBackRecord tmp = new MsgCallBackRecord();
            tmp.setRequestType(requestType);
            tmp.setCreateTime(new Date());
            tmp.setOrderNo(orderNo);
            tmp.setSellerId(sellerId);
            tmp.setAgentCode(agentCode);
            tmp.setSendTime(sendTime);
            tmp.setVersion(version);
            tmp.setBody(body);
            //回调消息记录
            callBackRecord = msgCallBackService.saveMsgCallBackRecord(tmp);
        } catch (Exception e) {
            logger.error("保存回调消息异常", e);
            return genResponseJson(headerJson, new ResultVo("W", 2, "保存回调信息异常:cause by :" + e.getMessage()));

        }

        //出单回调信息详细记录
        if (RequestTypeEnum.car_policy_sync.getValue().equals(requestType)) {

            //异步处理业务结果
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        //消息解析
                        msgCallBackService.parseMsgCallBack(callBackRecord);
                        //回调处理
                        carInsurService.postDueCallBack(orderNo);
                    } catch (Exception e) {
                        logger.error("参数解析异常", e);
                    }

                }
            }).start();
        }
        //响应设置
        return genResponseJson(headerJson, new ResultVo("T"));
    }

    /**
     * 车险券创建订单
     */
    @RequestMapping(value = "/validate/createLocalOrder")
    public ModelAndView createLocalOrder(@RequestParam("userId") Long userId, @RequestParam("bizType") String bizType, @RequestParam("bizNo") String bizNo, HttpServletResponse response) throws IOException {


        //参数校验
        if (userId == null) {
            response.sendRedirect(ERROR_PAGE + URLEncoder.encode("用户Id不能为空","UTF-8"));
            return null;
        }

        String orderNo;
        try {
            orderNo = carInsurService.createOrder(userId, bizType, bizNo);
        } catch (Exception e) {
            logger.error("创建订单异常", e);
            response.sendRedirect(ERROR_PAGE + URLEncoder.encode(e.getMessage(),"UTF-8"));
            return null;
        }
        //获取url
        String url = configService.getCfgValue(URL_CFG_KEY);
        if (StringUtils.isEmpty(url)) {
            logger.error("act_config_info 表未配置key:{}", URL_CFG_KEY);
            response.sendRedirect(URLEncoder.encode(ERROR_PAGE + "act_config_info 表未配置key:" + URL_CFG_KEY,"UTF-8"));
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?").append("partnerOrderNo=").append(orderNo);
        sb.append("&").append("agentCode=").append("W02412603");
        sb.append("&").append("spsource=").append("P05-SHANJINKEJI-001");
        sb.append("&").append("accountType=").append("5");
        logger.info(sb.toString());
        response.sendRedirect(sb.toString());
        return null;
    }


    @ResponseBody
    @RequestMapping("/validate/getInsurInfo")
    public CommonResultVo<InsureBaseInfo> getInsurInfo(@RequestParam("bizNo") String bizNo, @RequestParam("bizType") String bizType, @RequestParam("userId") Long userId) {


        if (StringUtils.isEmpty(bizNo) || StringUtils.isEmpty(bizType) || null == userId) {
            logger.error("参数不能为空");
            return new CommonResultVo<>("001", "参数不能为空");
        }
        //获取订单编号
        String orderNo = carInsurService.getOrderByBiz(userId, bizNo, bizType);
        if (StringUtils.isEmpty(orderNo)) {
            return new CommonResultVo<>("002", "参数无效");
        }
        final InsureBaseInfo baseInfoByBizNo = msgCallBackService.getBaseInfoByBizNo(orderNo);
        CommonResultVo resultVo = new CommonResultVo<>("000");
        resultVo.setData(baseInfoByBizNo);
        return resultVo;
    }

    /**
     * head元素生成
     *
     * @return
     */
    private JSONObject genHeaderJson(String version, String requestType, String sellerId, String orderNo) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CallBackHeaderEnum.VERSION.getHeadName(), version);
        jsonObject.put(CallBackHeaderEnum.REQUESTTYPE.getHeadName(), requestType);
        jsonObject.put(CallBackHeaderEnum.SELLERID.getHeadName(), sellerId);
        jsonObject.put(CallBackHeaderEnum.SENDTIME.getHeadName(), sdf.format(new Date()));
        jsonObject.put(CallBackHeaderEnum.AGENTCODE.getHeadName(), CallBackHeaderEnum.AGENTCODE.getDefaultValue());
        jsonObject.put(CallBackHeaderEnum.PARTNERORDERNO.getHeadName(), orderNo);

        return jsonObject;
    }

    private String getErrorRespJson(String code, String message) {
        CommonResultVo vo = new CommonResultVo(code, message);
        return JSON.toJSONString(vo);
    }


    private JSONObject genResponseJson(JSONObject headerJson, ResultVo vo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Header", headerJson);
        jsonObject.put("Response", vo);
        logger.info("响应报文:{}", jsonObject);
        return jsonObject;
    }

}
