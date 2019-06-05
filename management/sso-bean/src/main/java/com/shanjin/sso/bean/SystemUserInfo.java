package com.shanjin.sso.bean;

import java.io.Serializable;
import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 系统用户
 * authority_user_info
 * @author Huang yulai
 *
 */
public class SystemUserInfo extends Model<SystemUserInfo> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final SystemUserInfo dao = new SystemUserInfo();

	private Long id;
	
	private String userName;
	
	private String realName;
	
	private String accountName;
	
	private String accountBank;
	
	private String accountNumber;
	
	private String createName;
	
	private String updateName;

	private String psw;
	
	private String pswHints;

	private String email;

	private String phone;

	private String remark;

	private Long province;

	private Long city;
	
	private String provinceDesc;

	private String cityDesc;

	private int disabled;
	
	private int isAdmin;
	
	private int isDel;
	
	private int isAccount;
	
	private int userType;
	
	private int balance;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Long total;

	public SystemUserInfo() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}

	public String getPswHints() {
		return pswHints;
	}

	public void setPswHints(String pswHints) {
		this.pswHints = pswHints;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getProvince() {
		return province;
	}

	public void setProvince(Long province) {
		this.province = province;
	}

	public Long getCity() {
		return city;
	}

	public void setCity(Long city) {
		this.city = city;
	}

	public int getDisabled() {
		return disabled;
	}

	public void setDisabled(int disabled) {
		this.disabled = disabled;
	}

	public int getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountBank() {
		return accountBank;
	}

	public void setAccountBank(String accountBank) {
		this.accountBank = accountBank;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}

	public String getProvinceDesc() {
		return provinceDesc;
	}

	public void setProvinceDesc(String provinceDesc) {
		this.provinceDesc = provinceDesc;
	}

	public String getCityDesc() {
		return cityDesc;
	}

	public void setCityDesc(String cityDesc) {
		this.cityDesc = cityDesc;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getIsAccount() {
		return isAccount;
	}

	public void setIsAccount(int isAccount) {
		this.isAccount = isAccount;
	}
	
	
	
}
