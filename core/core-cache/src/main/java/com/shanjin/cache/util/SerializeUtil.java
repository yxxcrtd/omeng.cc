package com.shanjin.cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * @author Huangyulai
 *
 */
public class SerializeUtil {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(SerializeUtil.class);

	public static byte[] serialize(Object object) {
		if(object==null) return null;
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public static Object deserialize(byte[] bytes) {
		if(bytes==null) return null;
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
}
