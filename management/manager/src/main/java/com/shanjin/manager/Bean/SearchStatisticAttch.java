package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

public class SearchStatisticAttch extends Model<SearchStatisticAttch> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final SearchStatisticAttch dao = new SearchStatisticAttch();

	private String name; // 广告名称

	private String slider_type; // 广告类型

	private Date join_time; // 开始时间

	private Date overdue_time; // 结束时间

	private int slider_status; // 广告状态

	private int pics_id; // 图片ID
	private Long total;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlider_type() {
		return slider_type;
	}

	public void setSlider_type(String slider_type) {
		this.slider_type = slider_type;
	}

	public Date getJoin_time() {
		return join_time;
	}

	public void setJoin_time(Date join_time) {
		this.join_time = join_time;
	}

	public Date getOverdue_time() {
		return overdue_time;
	}

	public void setOverdue_time(Date overdue_time) {
		this.overdue_time = overdue_time;
	}

	public int getSlider_status() {
		return slider_status;
	}

	public void setSlider_status(int slider_status) {
		this.slider_status = slider_status;
	}

	public int getPics_id() {
		return pics_id;
	}

	public void setPics_id(int pics_id) {
		this.pics_id = pics_id;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
