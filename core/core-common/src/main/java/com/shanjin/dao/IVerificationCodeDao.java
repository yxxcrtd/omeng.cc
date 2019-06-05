package com.shanjin.dao;

import java.util.Map;

public interface IVerificationCodeDao {

    /** 更新验证码,最后登陆时间 */
    int updateVerificationInfo(Map<String, Object> paramMap);

	/**
	 * 临时测试使用-将接口日志信息插入到数据库
	 */
	public int saveSystemLog(Map<String,Object> param);
}
