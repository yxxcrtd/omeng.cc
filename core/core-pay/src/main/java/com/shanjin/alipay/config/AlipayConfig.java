package com.shanjin.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *提示：如何获取安全校验码和合作身份者ID
 *1.用你的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置你的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串(测试环境)
	public static String partner_test = "2088421663068282";
	// 商户的私钥(测试环境)
	public static String private_key_test = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ5gN0Qa1AYJhrZRe2VoTM9YEinDf/r9VJbcw8udVbuINw85WFJFuxWQMvwzcfivf1USlsZqjYTNdJPWaYJhqZTLCU7kigQJyIbxFkbxJvdeGG2ZLYp269i8zeAPib8inYh/e71L3PpYJ71JL5qClOK4AZ+ERVVnVX+6tkC6OAy1AgMBAAECgYEAiNT8Kr07HhQw7WgswoqAgkvHNYoREpq48La/+zVuEt43I9IZustAPfsd8cPI3mFSFABBgCAjMPAJnokLE3ipd+aQtYjPa+4+ajhzkOqgN3cRiSisHHP44o8dthjKbzjFdG4IZqOZRgfFBwUF9uGZ6N1WFEhv/6paaHFj5304LEECQQDMk83A9MuOpwGMFSf75DjVujJNyBsZcdzgunWewTe1JSGGedByAZl6Olxl1PdmhyJO9tTA5ZYWsDKP0Co4P9cpAkEAxi9s2+iIvjcRKxtR1sFo/E41GXnwKj7A6YCrfU3+vvdzrfqSF5uoemke1T5RFCWmC8iMijmBpzVe6OM1XUk2rQJAXa/ukUhKFGk9Fyb1KbEpqg9cTCyw5GU1ryEVDoAT//wiL1CH+j3rSIODnwr5xdVrC9iUNEawBnHopd3CCAKMiQJAZIDacK784ZGleDnTh9fveLvke7X10EagqAmjbdkmzgtJM7p1adl7WBCFHVkL3u60xRcABnzhPj13kxzeDB+7xQJAAT8jzme0LL8Vwz/OzV2oeEZH0ygnzdM6uyT7BFcG8u2tSx1/Itb9ijEC0ZKkr/En9SkJ5kZnjY/yvFpZ4be1hg==";
	//支付宝账号(测试环境)
	public static String seller_id_test = "486085043@qq.com";

	// 支付宝的公钥，无需修改该值(测试环境)
	public static String ali_public_key_test = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";


	
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088911386080004";
	// 商户的私钥
	//public static String private_key = "t7uyimzvn5rnoy0v8iyunbor58sc0m5t";
	public static String private_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKkJ6FulrT2XvCnXg50DMz9jI9SyDEDJipxA0Kow1MQIxvUbrbJTcdh8PpQTsTnDpGjimEbAblHKzK1VjmpfWzZ9RfUiaQ+WDRlh7G5Cpj6DJsk+diqDQd/ukuiHRcDBi9tcqqDweYt732QJc8lN6fPHLIbhIIPDMcUByQddK7QlAgMBAAECgYBC62oyhgdsf9p9EhXHnhG2wW/Y71fMej1GmJ5DEivZI8RoGroMA97pHl7Dznv16072Ouaf8+R4uvmGkX1c/T0lUzHmP1CQv4yx/mp8xignSJtZaTRA93tq3g0fjgHMVJkOU9j5GFxQtRJezC19PoX+4i1FZbirPXSrspEPGE+oTQJBAN1dLbpHJ6bQb6rKbUliiTNRP+3QtXC82+aEFFp+yC/UWn2I4IRv25Au1SYpCO1KMURUIuWUdedlnhyoKkta3acCQQDDfNHGX8+kCTX+aFB50BUDtrEfffpHoUgFQXrqoA1V3A+8u4AOXblXALCHbVnZWskIEX7IEfW+ChXzdSu3TFFTAkEAtpTPTbC41M9g+2bhg0Dh11DxwM5/iRBM9DIGs6mUplapmJdYUAQO/jqSlloMQeQLBMe8zM2J/iUDp7FQyTyWSwJARJpOJ9bB0Kgm2aQT8duzND1txUZ5iZ+w3Z9QGnyWkXYL08jdNK1xeHXWfYBDksKIYBt7qYyb99gkQe7xq37N3wJBAJHbBmN4KpoA9tLA7BEwQ+Y2CauMhtAwOloaYiUHXm/KE2lVUOSbtzeV6SydAUX3G6NbGRUMLWeks9MAFFxg7fI=";
	//支付宝账号
	public static String seller_id = "3239356291@qq.com";

	// 支付宝的公钥，无需修改该值
	public static String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	
	
	
	
	// ↑↑↑↑↑↑↑↑↑↑请在这里配置你的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";

	// 签名方式 不需修改
	public static String sign_type = "RSA";

}
