package com.shanjin.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商户提现信息表Dao
 */
public interface IMerchantWithdrawDao {

    /** 查询商户已绑定的提现账户数量 */
    int selectWithdrawNoCount(Map<String, Object> paramMap);

    /** 申请提现的时候，可选提现账户信息查询 */
    List<Map<String, Object>> selectWithdrawNo(Map<String, Object> paramMap);

    /** 绑定银行卡的信息保存 */
    int insertMerchantWithdraw(Map<String, Object> paramMap);
    
    /**
     * 解除银行卡绑定
     * @param withdrawId
     * @return
     */
    int unbindBankCard(Long withdrawId);
    /**
     * 修改支付密码
     * @param paramMap 
     * @return
     */
    int updPayPassword(Map<String, Object> paramMap);
    
    /**
     * 找回密码时验证银行卡信息是否存在
     * @param paramMap
     * @return
     */
    int isExistOfWithdraw(Map<String, Object> paramMap);
    
    Integer selectWithdrawNoByCardNo(Map<String, Object> paramMap);
    
    BigDecimal getAllMoney(Map<String, Object> paramMap);
    
    /**
     * 获取一段时间内的收入
     * @param paramMap
     * @return
     */
    BigDecimal getMoneyByTime(Map<String, Object> paramMap);
    
    BigDecimal getAllWithdrawMoney(Map<String, Object> paramMap);
    
    /**
     * 是否关闭商店里的体现功能
     * @return
     */
    int isDisableWithDraw();
    
}
