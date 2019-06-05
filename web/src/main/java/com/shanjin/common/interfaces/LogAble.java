package com.shanjin.common.interfaces;

import org.apache.log4j.Logger;

/**
 * @author xiechanglei
 *
 * Description	: Log4j 抽离成接口,使调用简单，不需要在类中声明Logger对象
 *                使用:  
 *                class Demmo implements LogAble{ //implements LogAble
 *                  public void someRun(){
 *                      logger.info("test here");//调用logger
 *                  }
 *                }
 *                至于为什么向上抽成接口而不是抽象类，我想我不需要解释了吧
 * CreateTime	: 2015-5-14 14:50:09
 * UpdateTime	: 2015-5-14 14:50:13
 */
public interface LogAble {
    static final Logger logger = Logger.getLogger(LogAble.class);
}
