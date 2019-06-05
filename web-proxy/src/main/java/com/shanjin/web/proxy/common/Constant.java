package com.shanjin.web.proxy.common;

public class Constant {
	// ********************客户端类型定义（1：安卓；2：ios）*******************
	public static final int PUSH_CLIENT_TYPE_ANDROID = 1; //
	public static final int PUSH_CLIENT_TYPE_IOS = 2;
	// ********************IOS推送证书参数定义*******************
	public static final String IOS_PUSH_KEYSTORE = "keystore";
	public static final String IOS_PUSH_PASSWORD = "password";
	public static final String IOS_PUSH_PRODUCTION = "production";
	public static final String IOS_PUSH_THREADS = "threads";

	public static final String USER = "user";
	public static final String MERCHANT = "merchant";

	public static final String IOS_CERT_TYPE = "iosCertType";
	public static final String IOS_USER_CERT = "iosUserCert";
	public static final String IOS_MERCHANT_CERT = "iosMerchantCert";
	
	// ios用户端推送证书
	public static final class CertOfUser {
		public static final String keystore = "/usr/local/omengUser.p12";
		public static final String password = "123456";
		public static final boolean production = false;
		public static final int threads = 30;
	}

	// ios商户端推送证书
	public static final class CertOfMerchant {
		public static final String keystore = "/usr/local/omengMerchant.p12";
		public static final String password = "123456";
		public static final boolean production = false;
		public static final int threads = 30;
	}
	
	
}
