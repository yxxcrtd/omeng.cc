package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class MerPhotoStatic extends Model<MerPhotoStatic>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MerPhotoStatic dao=new MerPhotoStatic();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
