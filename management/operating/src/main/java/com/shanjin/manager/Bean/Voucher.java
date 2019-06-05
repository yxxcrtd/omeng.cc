package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

public class Voucher extends Model<Voucher> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Voucher dao = new Voucher();

	private Integer id;

	private Integer coupons_type;
	private Date cutoff_time;
	private Integer price;
	private Integer count;
	private Integer is_del;
	private String app_type;
	private Long total;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCoupons_type() {
		return coupons_type;
	}

	public void setCoupons_type(Integer coupons_type) {
		this.coupons_type = coupons_type;
	}

	public Date getCutoff_time() {
		return cutoff_time;
	}

	public void setCutoff_time(Date cutoff_time) {
		this.cutoff_time = cutoff_time;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getIs_del() {
		return is_del;
	}

	public void setIs_del(Integer is_del) {
		this.is_del = is_del;
	}

	public String getApp_type() {
		return app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
