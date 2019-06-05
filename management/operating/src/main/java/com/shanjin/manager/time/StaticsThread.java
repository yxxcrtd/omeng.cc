package com.shanjin.manager.time;

public class StaticsThread extends Thread{
	
@Override
public void run() {
	
   //统计商户运营信息，生成excel表格，提供用户下载，每天更新一次
	StatisticalUtil.merchantStatis();
}
}
