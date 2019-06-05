package com.shanjin.goldplan.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.shanjin.goldplan.model.Prize;
/**
 * 抽奖工具类
 * @author CUIJIAJUN
 *
 */
public class PrizeMathRandom {
	/**
     * 根据Math.random()产生一个double型的随机数，判断每个奖品出现的概率
     * @param prizes
     * @return random：奖品列表prizes中的序列（prizes中的第random个就是抽中的奖品）
     */
    public static int getPrizeIndex(List<Prize> prizes) {
        DecimalFormat df = new DecimalFormat("######0.00");  
        int random = -1;
        try{
            //计算总权重
            double sumWeight = 0;
            for(Prize p : prizes){
                sumWeight += p.getPrize_weight();
            }

            //产生随机数
            double randomNumber;
            randomNumber = Math.random();

            //根据随机数在所有奖品分布的区域并确定所抽奖品
            double d1 = 0;
            double d2 = 0;          
            for(int i=0;i<prizes.size();i++){
                d2 += Double.parseDouble(String.valueOf(prizes.get(i).getPrize_weight()))/sumWeight;
                if(i==0){
                    d1 = 0;
                }else{
                    d1 +=Double.parseDouble(String.valueOf(prizes.get(i-1).getPrize_weight()))/sumWeight;
                }
                if(randomNumber >= d1 && randomNumber <= d2){
                    random = i;
                    break;
                }
            }
            if(random == -1){
            	throw new Exception("全部奖品都不可抽取。");
            }
        }catch(Exception e){
            System.out.println("生成抽奖随机数出错，出错原因：" +e.getMessage());
        }
        return random;
    }
    
    public static int getRandom(int min,int max){
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }
    
    public static void main(String[] args){
    	System.out.println(PrizeMathRandom.getRandom(0, 2));
    }
}
