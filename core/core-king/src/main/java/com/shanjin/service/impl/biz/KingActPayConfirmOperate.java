package com.shanjin.service.impl.biz;

import com.shanjin.dao.ActivityRelateMapper;
import com.shanjin.dao.KingMemberMapper;
import com.shanjin.dao.KingUserAssetMapper;
import com.shanjin.model.king.KingActOrder;
import com.shanjin.model.king.KingMember;
import com.shanjin.model.king.KingUserAsset;
import com.shanjin.service.ICplanKingService;
import com.shanjin.service.impl.BizPayConfirmOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/11/3
 * @desc 业务类型扩展
 */
@Service("kingActPayConfirmOperate")
public class KingActPayConfirmOperate extends BizPayConfirmOperate<KingActOrder> {

    @Autowired
    private KingUserAssetMapper kingUserAssetMapper;
    @Autowired
    private KingMemberMapper kingMemberMapper;
    @Autowired
    private ActivityRelateMapper activityRelateMapper;
    @Autowired
    private ICplanKingService cplanKingService;

    private static final Logger logger = LoggerFactory.getLogger(KingActPayConfirmOperate.class);
    @Override
    protected String[] getBizTypes() {
        return new String[]{"KING_ACT","KING_ACT_MERCHANT"};
    }

    @Override
    protected void doOperate(KingActOrder order) {
        //查询消费金金额
        Map<String, Object> pkgDetailMap = activityRelateMapper.getPkgDetail(Long.valueOf(order.getBizNo()));
        if (pkgDetailMap == null) {
            logger.error("bizType：{}，关联的bizNo:{}找不到相关业务数据", order.getBizType(), order.getBizNo());
            throw new RuntimeException("找不到关联的业务号信息");
        }
        BigDecimal consGoldValue = (BigDecimal) pkgDetailMap.get("consumeGoldValue");
        int pkgLimit = (int) pkgDetailMap.get("pkgLimit");
        BigDecimal consumeGoldLimit = (BigDecimal) pkgDetailMap.get("consumeGoldLimit");

        //新增王牌会员和会员资产表数据
        Calendar calendar = Calendar.getInstance();
        Date startTime = calendar.getTime();
        calendar.add(Calendar.YEAR, pkgLimit);
        Date endTime = calendar.getTime();
        KingMember member = new KingMember();
        member.setUserId(order.getUserId());
        member.setStartTime(startTime);
        member.setEndTime(endTime);
        member.setInviteCode(order.getInviteCode());
        member.setOrderNo(order.getOrderNo());
        member.setCreateTime(startTime);
        member.setCreator("system_pay_callback");
        member.setConsGoldDeno(consGoldValue);
        kingMemberMapper.saveEntity(member);
        //新增用户资产表
        KingUserAsset asset = new KingUserAsset();
        asset.setUserId(order.getUserId());
        asset.setCreator("system_pay_callback");
        asset.setCreateTime(startTime);
        asset.setAssetAmount(consGoldValue);
        asset.setBlockAmount(BigDecimal.ZERO);
        asset.setLimitedAmount(consumeGoldLimit);
        kingUserAssetMapper.saveEntity(asset);
        //绑定私人助理
        boolean flag = cplanKingService.bindPersAss(order.getInviteCode(),order.getUserId(),order.getCreateTime());
        if(!flag){
            throw new RuntimeException("绑定私人助理失败");
        }
    }
}
