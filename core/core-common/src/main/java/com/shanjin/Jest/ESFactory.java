package com.shanjin.Jest;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.shanjin.common.util.StringUtil;

public class ESFactory {
	private static JestHttpClient client;

	private ESFactory() {
	}

	public synchronized static JestHttpClient getClient() {
		String address = "";
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("elasticsearch.properties");
			address = StringUtil.null2Str(props.get("elasticsearch.address"));
			if (address==""){
				 address="http://192.168.1.202:9200";
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(address).connTimeout(Integer.MAX_VALUE).readTimeout(Integer.MAX_VALUE).multiThreaded(true).build());
		client = (JestHttpClient) factory.getObject();
		return client;
	}

	public static void main(String[] args) {
		JestHttpClient client = ESFactory.getClient();
		System.out.println(client.getAsyncClient());
		String str = "我,.?!，。要去,.?!，。吃饭啦,.?!，。！";
		str = str.replaceAll("[\\pP‘’“”]", "");
		System.out.println(str);
		client.shutdownClient();
	}
}
