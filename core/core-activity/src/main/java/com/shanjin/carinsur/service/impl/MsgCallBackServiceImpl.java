package com.shanjin.carinsur.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.carinsur.dao.*;
import com.shanjin.carinsur.model.*;
import com.shanjin.carinsur.service.MsgCallBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/31
 * @desc TODO
 */
@Service("msgCallBackService")
public class MsgCallBackServiceImpl implements MsgCallBackService {

    @Autowired
    private MsgCallBackRecordMapper callBackRecordMapper;

    @Autowired
    private MsgComInsurInfoMapper comInsurInfoMapper;

    @Autowired
    private MsgTraInsurInfoMapper traInsurInfoMapper;
    @Autowired
    private MsgOrderCallbackBaseInfoMapper orderCallbackBaseInfoMapper;


    @Override
    public MsgCallBackRecord saveMsgCallBackRecord(MsgCallBackRecord record) {
        callBackRecordMapper.saveEntity(record);
        return record;
    }

    @Override
    public void parseMsgCallBack(MsgCallBackRecord record) {
        JSONObject jsonObject = JSON.parseObject(record.getBody());

        JSONObject requestJson = jsonObject.getJSONObject("Request");

        parseAndSaveTraInsurInfo(record.getOrderNo(), requestJson);
        parseAndSaveComInsurInfo(record.getOrderNo(), requestJson);
        parseAndSaveCallbackBaseInfo(record.getOrderNo(), requestJson);

    }

    @Override
    public InsureBaseInfo getBaseInfoByBizNo(String orderNo) {

        if (!StringUtils.isEmpty(orderNo)) {
            MsgOrderCallbackBaseInfo baseInfo = orderCallbackBaseInfoMapper.getEntityByKey(orderNo);
            MsgComInsurInfo comInsurInfo = comInsurInfoMapper.getEntityByKey(orderNo);
            MsgTraInsurInfo traInsurInfo = traInsurInfoMapper.getEntityByKey(orderNo);
            if (null != baseInfo) {
                String insurOrderNo = "";
                BigDecimal premium = BigDecimal.ZERO;
                if (traInsurInfo != null) {
                    insurOrderNo = traInsurInfo.getSunOrderNo();
                    premium = premium.add(new BigDecimal(traInsurInfo.getPremium()));
                }
                if (comInsurInfo != null) {
                    if (StringUtils.isEmpty(insurOrderNo)) {
                        insurOrderNo = comInsurInfo.getSunOrderNo();
                    }
                    premium = premium.add(new BigDecimal(comInsurInfo.getPremium()));
                }

                //创建保单基本信息
                InsureBaseInfo insureBaseInfo = new InsureBaseInfo();
                insureBaseInfo.setBrand(baseInfo.getBrand());
                insureBaseInfo.setEngineNo(baseInfo.getEngineNo());
                insureBaseInfo.setLicenseNo(baseInfo.getLicenseNo());
                insureBaseInfo.setOwnerIdNo(baseInfo.getOwnerIdNo());
                insureBaseInfo.setOwnerMobile(baseInfo.getOwnerMobile());
                insureBaseInfo.setOwnerName(baseInfo.getOwnerName());
                insureBaseInfo.setVehicleFrameNo(baseInfo.getVehicleFrameNo());

                insureBaseInfo.setOrderNo(insurOrderNo);
                insureBaseInfo.setPremium(premium);

                return insureBaseInfo;
            }
        }
        return null;
    }


    private void parseAndSaveTraInsurInfo(String orderNo, JSONObject msgJson) {
        MsgTraInsurInfo insurInfo = new MsgTraInsurInfo(orderNo);
        JSONObject traInsurInfoJson = msgJson.getJSONObject("traOrderInfo");
        if (null != traInsurInfoJson && traInsurInfoJson.size() > 0) {
            parseAbsInsurInfo(insurInfo, traInsurInfoJson);
            traInsurInfoMapper.saveEntity(insurInfo);
        }
    }

    private void parseAndSaveComInsurInfo(String orderNo, JSONObject msgJson) {
        MsgComInsurInfo insurInfo = new MsgComInsurInfo(orderNo);
        JSONObject comInsurInfoJson = msgJson.getJSONObject("comOrderInfo");
        if (null != comInsurInfoJson && comInsurInfoJson.size() > 0) {
            parseAbsInsurInfo(insurInfo, comInsurInfoJson);
            //解析险种编码
            JSONArray jsonArray = comInsurInfoJson.getJSONArray("commercialInfo");
            if (null != jsonArray && jsonArray.size() > 0) {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject comInfoJson = jsonArray.getJSONObject(i);
                    String code = comInfoJson.getString("insureCode");
                    if (!StringUtils.isEmpty(code)) {
                        sb.append(code).append(",");
                    }
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                    insurInfo.setComInsurKinds(sb.toString());
                }
            }
            //保存
            comInsurInfoMapper.saveEntity(insurInfo);
        }
    }

    private void parseAndSaveCallbackBaseInfo(String orderNo, JSONObject msgJson) {

        JSONObject veInfoJson = msgJson.getJSONObject("vehicleInfo");
        JSONObject ownerInfoJson = msgJson.getJSONObject("ownerInfo");
        JSONObject appInfoJson = msgJson.getJSONObject("applicantInfo");
        JSONObject insuredInfoJson = msgJson.getJSONObject("insuredInfo");
        JSONObject deliverInfoJson = msgJson.getJSONObject("deliverInfo");
        JSONObject otherInfoJson = msgJson.getJSONObject("otherInfo");

        MsgOrderCallbackBaseInfo baseInfo = new MsgOrderCallbackBaseInfo(orderNo);
        if (null != veInfoJson) {
            baseInfo.setVehicleFrameNo(veInfoJson.getString("vehicleFrameNo"));
            baseInfo.setLicenseNo(veInfoJson.getString("licenseNo"));
            baseInfo.setBrand(veInfoJson.getString("brand"));
            baseInfo.setEngineNo(veInfoJson.getString("engineNo"));
            baseInfo.setRegisterDate(veInfoJson.getString("registerDate"));
        }
        if (null != ownerInfoJson) {
            baseInfo.setOwnerName(ownerInfoJson.getString("ownerName"));
            baseInfo.setOwnerMobile(ownerInfoJson.getString("ownerMobile"));
            baseInfo.setOwnerIdNo(ownerInfoJson.getString("ownerIdNo"));
            baseInfo.setOwnerEmail(ownerInfoJson.getString("ownerEmail"));
        }
        if (null != appInfoJson) {
            baseInfo.setApplicantName(appInfoJson.getString("applicantName"));
            baseInfo.setApplicantMobile(appInfoJson.getString("applicantMobile"));
            baseInfo.setApplicantIdNo(appInfoJson.getString("applicantIdNo"));
            baseInfo.setApplicantEmail(appInfoJson.getString("applicantEmail"));
        }
        if (null != insuredInfoJson) {
            baseInfo.setInsuredName(insuredInfoJson.getString("insuredName"));
            baseInfo.setInsuredMobile(insuredInfoJson.getString("insuredMobile"));
            baseInfo.setInsuredIdNo(insuredInfoJson.getString("insuredIdNo"));
            baseInfo.setInsuredEmail(insuredInfoJson.getString("insuredEmail"));
        }
        if (null != deliverInfoJson) {
            baseInfo.setAddress(deliverInfoJson.getString("address"));
            baseInfo.setReceiverName(deliverInfoJson.getString("receiverName"));
            baseInfo.setReceiverTel(deliverInfoJson.getString("receiverTel"));
        }
        if (null != otherInfoJson) {
            baseInfo.setUnSettlementCost(otherInfoJson.getString("unSettlementCost"));
        }

        orderCallbackBaseInfoMapper.saveEntity(baseInfo);
    }

    private void parseAbsInsurInfo(AbsInsurInfo absInsurInfo, JSONObject InsurInfoJson) {

        absInsurInfo.setCallbackDetail(InsurInfoJson.getString("callback_detail"));
        absInsurInfo.setCallbackPay(InsurInfoJson.getString("callback_pay"));
        absInsurInfo.setPcPayUrl(InsurInfoJson.getString("pc_payUrl"));
        absInsurInfo.setPayTime(InsurInfoJson.getString("payTime"));
        absInsurInfo.setPolicyNo(InsurInfoJson.getString("policyNo"));
        absInsurInfo.setPremium(InsurInfoJson.getString("premium"));
        absInsurInfo.setProposalNo(InsurInfoJson.getString("proposalNo"));
        absInsurInfo.setStartDate(InsurInfoJson.getString("startDate"));
        absInsurInfo.setEndDate(InsurInfoJson.getString("endDate"));
        absInsurInfo.setOutPayId(InsurInfoJson.getString("outPayId"));
        absInsurInfo.setSunOrderNo(InsurInfoJson.getString("orderNo"));
        absInsurInfo.setTax(InsurInfoJson.getString("tax"));

    }

}
