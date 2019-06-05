package com.shanjin.sso.bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 系统资源
 * authority_resource_info
 * @author Huang yulai
 *
 */
public class SystemResource extends Model<SystemResource>{
	
	private static final long serialVersionUID = 1L;

	public static final SystemResource dao = new SystemResource();

	private Long id;
	
	private String resName;
	
	private String remark;
	
	private String linkPath;
	
	private int disabled;
	
	private int type;
	
	private Long fatherId;
	
	private int rank;
	
	private int isLeaf;
	
	private int isCommon;
	
	private Long total;

	public SystemResource(){
		
	}
	
	public Long getId() {
		return id;
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	public int getDisabled() {
		return disabled;
	}

	public void setDisabled(int disabled) {
		this.disabled = disabled;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFatherId() {
		return fatherId;
	}

	public void setFatherId(Long fatherId) {
		this.fatherId = fatherId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(int isLeaf) {
		this.isLeaf = isLeaf;
	}

	public int getIsCommon() {
		return isCommon;
	}

	public void setIsCommon(int isCommon) {
		this.isCommon = isCommon;
	}
	
	
	
}
