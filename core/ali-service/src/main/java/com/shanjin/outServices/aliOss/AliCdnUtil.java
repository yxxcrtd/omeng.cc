package com.shanjin.outServices.aliOss;

import java.util.Map;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aliyun.api.AliyunClient;
import com.aliyun.api.DefaultAliyunClient;
import com.aliyun.api.cdn.cdn20141111.request.DescribeCdnDomainDetailRequest;
import com.aliyun.api.cdn.cdn20141111.request.RefreshObjectCachesRequest;
import com.aliyun.api.cdn.cdn20141111.response.DescribeCdnDomainDetailResponse;
import com.aliyun.api.cdn.cdn20141111.response.RefreshObjectCachesResponse;
import com.aliyun.api.domain.GetDomainDetailModel;
import com.taobao.api.ApiException;

/**
 * 封装阿里CDN API
 * 
 * @author Revoke Yu
 *
 */
public class AliCdnUtil {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(AliCdnUtil.class);

	private static final String CDN_ENDPOINT = "http://cdn.aliyuncs.com/";

	/**
	 * 同步刷新缓存 或 目录
	 * 
	 * @param path
	 *            CDN 中 文件或目录的全路径
	 * @param isDirectory
	 *            目录：true 文件：false 每天最多可以刷新2000个文件(URL)和100个目录。刷新任务生效时间大约为5分钟。
	 * @throws ApiException 
	 */
	public static boolean refresh(String path, boolean isDirectory) throws ApiException {

		AliyunClient client = new DefaultAliyunClient(CDN_ENDPOINT,
				AliOssUtil.ACCESS_ID, AliOssUtil.ACCESS_KEY);

		RefreshObjectCachesRequest refreshRequest = new RefreshObjectCachesRequest();

		String objectPath = AliOssUtil.getCDNPrefix() + path;

		refreshRequest.setObjectPath(objectPath);

		refreshRequest.setObjectType(isDirectory ? "Directory" : "File");

		try {

			RefreshObjectCachesResponse response = client
					.execute(refreshRequest);
			return response.isSuccess();
		} catch (ApiException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	
	/**
	 * 异步刷新缓存 或 目录
	 * 
	 * @param path
	 *            CDN 中 文件或目录的全路径
	 * @param isDirectory
	 *            目录：true 文件：false 每天最多可以刷新2000个文件(URL)和100个目录。刷新任务生效时间大约为5分钟。
	 * @throws ApiException 
	 */
	public static Future<RefreshObjectCachesResponse> asyncRefresh(String path, boolean isDirectory) throws ApiException  {

		AliyunClient client = new DefaultAliyunClient(CDN_ENDPOINT,
				AliOssUtil.ACCESS_ID, AliOssUtil.ACCESS_KEY);

		RefreshObjectCachesRequest refreshRequest = new RefreshObjectCachesRequest();

		String objectPath = AliOssUtil.getCDNPrefix() + path;

		refreshRequest.setObjectPath(objectPath);

		refreshRequest.setObjectType(isDirectory ? "Directory" : "File");

		return client.executeAsync(refreshRequest, null);
	}


	public static void main(String[] args) {
		String path = "/manFile/package/merchant/cbt_V_1.0.0.apk";
		try {
			AliCdnUtil.refresh(path,false);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
}
