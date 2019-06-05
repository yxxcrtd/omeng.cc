

package com.shanjin.incr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * @see
 * @author 	hurd@omeng.cc
 * @date	2016-08-19 09:55:17 中国标准时间
 * @version	v0.1
 * @desc    TODO
 */
public class Catalog {
	
  public static final String TABLE_NAME = "catalog";

	public static final String TABLE_ALIAS = "Catalog";
	
	public static final String COL_ID = "ID";
	
	public static final String COL_NAME = "NAME";
	
	public static final String COL_LEVEL = "LEVEL";
	
	public static final String COL_PARENTID = "PARENTID";
	
	public static final String COL_LEAF = "LEAF";
	
	public static final String COL_STATUS = "STATUS";
	
	public static final String COL_ISDEL = "IS_DEL";
	
	public static final String COL_ALIAS = "ALIAS";
	
	public static final String COL_DEMAND = "DEMAND";
	
	public static final String COL_RANK = "RANK";
	
	public static final String COL_ICONPATH = "ICON_PATH";
	
	public static final String COL_BIGICONPATH = "BIG_ICON_PATH";
	
	public static final String COL_ISHAVEGOODS = "IS_HAVE_GOODS";
	
	public static final String COL_ISTHIRD = "IS_THIRD";
	
	public static final String COL_LINK = "LINK";
	
	
	
	private Integer id;
	private String name;
	private Integer level;
	private Integer parentid;
	private Integer leaf;
	private Integer status;
	private Integer isDel;
	private String alias;
	private String demand;
	private Integer rank;
	private String iconPath;
	private String bigIconPath;
	private Integer isHaveGoods;
	private Integer isThird;
	private String link;

	public Catalog() {}

	public Catalog(Integer id) {
		this.id = id;
	}

	public void setId(Integer value) {
		this.id = value;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setLevel(Integer value) {
		this.level = value;
	}
	
	public Integer getLevel() {
		return this.level;
	}
	
	public void setParentid(Integer value) {
		this.parentid = value;
	}
	
	public Integer getParentid() {
		return this.parentid;
	}
	
	public void setLeaf(Integer value) {
		this.leaf = value;
	}
	
	public Integer getLeaf() {
		return this.leaf;
	}
	
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	public Integer getStatus() {
		return this.status;
	}
	
	public void setIsDel(Integer value) {
		this.isDel = value;
	}
	
	public Integer getIsDel() {
		return this.isDel;
	}
	
	public void setAlias(String value) {
		this.alias = value;
	}
	
	public String getAlias() {
		return this.alias;
	}
	
	public void setDemand(String value) {
		this.demand = value;
	}
	
	public String getDemand() {
		return this.demand;
	}
	
	public void setRank(Integer value) {
		this.rank = value;
	}
	
	public Integer getRank() {
		return this.rank;
	}
	
	public void setIconPath(String value) {
		this.iconPath = value;
	}
	
	public String getIconPath() {
		return this.iconPath;
	}
	
	public void setBigIconPath(String value) {
		this.bigIconPath = value;
	}
	
	public String getBigIconPath() {
		return this.bigIconPath;
	}
	
	public void setIsHaveGoods(Integer value) {
		this.isHaveGoods = value;
	}
	
	public Integer getIsHaveGoods() {
		return this.isHaveGoods;
	}
	
	public void setIsThird(Integer value) {
		this.isThird = value;
	}
	
	public Integer getIsThird() {
		return this.isThird;
	}
	
	public void setLink(String value) {
		this.link = value;
	}
	
	public String getLink() {
		return this.link;
	}
	

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("Level",getLevel())
			.append("Parentid",getParentid())
			.append("Leaf",getLeaf())
			.append("Status",getStatus())
			.append("IsDel",getIsDel())
			.append("Alias",getAlias())
			.append("Demand",getDemand())
			.append("Rank",getRank())
			.append("IconPath",getIconPath())
			.append("BigIconPath",getBigIconPath())
			.append("IsHaveGoods",getIsHaveGoods())
			.append("IsThird",getIsThird())
			.append("Link",getLink())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Catalog)){
			return false;
		} 
		if(this == obj){
			return true;
		} 
		Catalog other = (Catalog)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

