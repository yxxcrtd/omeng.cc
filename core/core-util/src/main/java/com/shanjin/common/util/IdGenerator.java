package com.shanjin.common.util;

import java.util.Random;

public class IdGenerator {

	public static Random random = new Random();

	/**
	 * 自动生成ID，规则为System.currentTimeMillis()+剩余随机数
	 * 
	 * @param length
	 *            期望生成随机数的长度
	 * @return
	 */
	public static Long generateID(int length) {
		String curRandom = "" + System.currentTimeMillis();
		int len = curRandom.length();
		if (len <= length) {
			len = length - len;
			for (int i = 0; i < len; i++) {
				curRandom += random.nextInt(10) + "";
			}
		} else {
			curRandom = curRandom.substring(0, length);
		}

		return Long.valueOf(curRandom);
	}

	/**
	 * 自动生成ID，规则为System.currentTimeMillis()+剩余随机数
	 * 
	 * @param length
	 *            期望生成随机数的长度
	 * @return
	 */
	public static String getOrderNo(int length) {
		String curRandom = "" + System.currentTimeMillis();
		int len = curRandom.length();
		if (len <= length) {
			len = length - len;
			for (int i = 0; i < len; i++) {
				curRandom += random.nextInt(10) + "";
			}
		} else {
			curRandom = curRandom.substring(0, length);
		}
		return curRandom;
	}

	public static int getRandomValue(int maxValue){
			return random.nextInt(maxValue);
	}
	
	public static void main(String[] args) {
		System.out.println(IdGenerator.generateID(18));
	}

}
