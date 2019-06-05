package com.aliyun.oss.testing;

import java.io.File;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.OSSObject;

/**
 * 该示例代码展示如何以追加的方式将文件或输入流写入到OSS。
 */
public class AppendObjectSample {
	
	private static final String ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    private static final String ACCESS_ID = "<your access key id>";
    private static final String ACCESS_KEY = "<your access key secret>";

    public static void main(String[] args) throws Exception {
    	
    	String bucketName = "<your bucket name>";
    	String key = "<object name>";
    	
    	try {	
    		// 创建OSSClient实例
    		OSSClient client = new OSSClient(ENDPOINT, ACCESS_ID, ACCESS_KEY);
    		
    		// 发起首次追加Object请求，注意首次追加需要设置追加位置为0
    		final String fileToAppend = "<file to append at first time>";
    		AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName, key, new File(fileToAppend));
    		// 设置追加位置为0
    		appendObjectRequest.setPosition(0L);
    		AppendObjectResult appendObjectResult = client.appendObject(appendObjectRequest);

    		// 发起第二次追加Object请求，追加位置为第一次追加后的Object长度
    		final String fileToAppend2 = "<file to append at second time>";
    		appendObjectRequest = new AppendObjectRequest(bucketName, key, new File(fileToAppend2));
    		// 设置追加位置为前一次追加文件的大小
    		appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
    		appendObjectResult = client.appendObject(appendObjectRequest);
    		OSSObject o = client.getObject(bucketName, key);
    		// 当前该Object的大小为两次追加文件的大小总和
    		System.out.println(o.getObjectMetadata().getContentLength());
    		// 下一个追加位置为前两次追加文件的大小总和
    		System.err.println(appendObjectResult.getNextPosition().longValue());
    	} catch (OSSException oe) {
    		System.out.println(oe.getMessage());
    	} catch (Exception e) {
    		
    	}
    }
}
