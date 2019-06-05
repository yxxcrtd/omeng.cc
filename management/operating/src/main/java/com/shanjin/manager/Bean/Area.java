package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 地区字典
 * area
 * @author Huang yulai
 *
 */
public class Area extends Model<Area>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Area dao = new Area();

	private Long id;
	
	private String area;
	
	private Long parent_id;
	
	private String index_str;
	
	public Area() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public String getIndex_str() {
		return index_str;
	}

	public void setIndex_str(String index_str) {
		this.index_str = index_str;
	}

	

}
