/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.oss.common.utils;

import static com.aliyun.oss.internal.OSSConstants.LOGGER_PACKAGE_NAME;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.ServiceException;

public class LoggerUtils {
	
	private static final Logger logger = Logger.getLogger(LOGGER_PACKAGE_NAME);
	
	// Set logger level to INFO specially if reponse error code is 404 in order to 
	// prevent from dumping a flood of logs when trying access to none-existent resources.
	private static List<String> errorCodeFilterList = new ArrayList<String>();
	static {
		errorCodeFilterList.add(OSSErrorCode.NO_SUCH_BUCKET);
		errorCodeFilterList.add(OSSErrorCode.NO_SUCH_KEY);
		errorCodeFilterList.add(OSSErrorCode.NO_SUCH_UPLOAD);
		errorCodeFilterList.add(OSSErrorCode.NO_SUCH_CORS_CONFIGURATION);
		errorCodeFilterList.add(OSSErrorCode.NO_SUCH_WEBSITE_CONFIGURATION);
		errorCodeFilterList.add(OSSErrorCode.NO_SUCH_LIFECYCLE);
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public static void setLevel(String levelAsString) {
		Level lvl = Level.toLevel(levelAsString, Level.DEBUG);
		LogManager.getRootLogger().setLevel(lvl);
		LogManager.getLogger(LOGGER_PACKAGE_NAME).setLevel(lvl);
	}
	
	public static String getLevel() {
		return logger.getLevel().toString();
	}
	
	public static <ExType> void logException(String messagePrefix, ExType ex) {
		
		assert(ex instanceof Exception);
		
		String detailMessage = messagePrefix + ((Exception) ex).getMessage();
		if (ex instanceof ServiceException &&
				errorCodeFilterList.contains(((ServiceException) ex).getErrorCode())) {
			logger.info(detailMessage);
		} else {
			logger.warn(detailMessage);
		}
	}
}
