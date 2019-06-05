package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class Catalog extends Model<Catalog>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Catalog dao=new Catalog();
	private int id;
	private String text;
	private boolean leaf;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	
}
