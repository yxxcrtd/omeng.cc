package com.shanjin.common.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class FTPUtil implements ApplicationContextAware {
	private static ApplicationContext appCtx;

	/**
	 * 
	 * @param path
	 *            上传到ftp服务器哪个路径下
	 * @param addr
	 *            地址
	 * @param port
	 *            端口号
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @throws Exception
	 */
	private static boolean connect(FTPClient ftp, String path, String addr, int port, String username, String password) throws Exception {
		boolean result = false;
		int reply;
		ftp.connect(addr, port);
		ftp.login(username, password);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			return result;
		}
		ftp.setBufferSize(1024 * 1024 * 10);
		String[] paths = path.split("/");
		for (int i = 0; i < paths.length; i++) {
			if (i == 0) {
				ftp.changeWorkingDirectory("/");
			} else {
				ftp.changeWorkingDirectory(paths[i - 1]);
			}
			ftp.makeDirectory(paths[i]);
		}
		int index = paths.length - 1;
		if (paths[index].equals("/")) {
			index--;
		}
		ftp.changeWorkingDirectory(paths[index]);
		result = true;
		return result;
	}

	/**
	 * 
	 * @param file
	 *            上传的文件或文件夹
	 * @throws Exception
	 */
	private static boolean upload(FTPClient ftp, String fileName, InputStream file) throws Exception {
		boolean result = false;
		result = ftp.storeFile(fileName, file);
		file.close();
		return result;
	}

	public static boolean uploadFile(String path, String fileName, InputStream fileStream) throws Exception {
		FTPClient ftp = new FTPClient();
		Properties properties;
		properties = PropertiesLoaderUtils.loadAllProperties("ftpConfig.properties");
		//ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
		connect(ftp, path, properties.getProperty("addr"), Integer.valueOf(properties.getProperty("port")), properties.getProperty("username"), properties.getProperty("password"));
		boolean bool = upload(ftp, fileName, fileStream);
		if (ftp.isConnected()) {
			ftp.disconnect();
		}
		return bool;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		appCtx = ctx;
	}

}
