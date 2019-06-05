package com.rong.cloud;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.apache.log4j.Logger;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rong.cloud.model.ChatroomInfo;
import com.rong.cloud.model.FormatType;
import com.rong.cloud.model.GroupInfo;
import com.rong.cloud.model.Message;
import com.rong.cloud.model.SdkHttpResult;
import com.rong.cloud.util.HttpUtil;
import com.shanjin.common.constant.Constant;

public class RongCloudApiHttpClient {

	private static Logger logger = Logger.getLogger(RongCloudApiHttpClient.class);

	private static final String RONGCLOUDURI = "http://api.cn.ronghub.com";

	private static final String UTF8 = "UTF-8";

//	private static final String appKey = "25wehl3uwo82w";  //开发环境

//	private static final String appSecret = "24k5q6qUN0ULxr";  //开发环境
	
	private static  String appKey = "qd46yzrf4jeqf";  //生产环境

	private static  String appSecret = "s8k8ID07ppzQ";  //生产环境
	
	public static void main(String args[]){
		String token = getRongCloudToken("1","searo","");
		System.out.println("rong cloud token is : "+token);
	}
	

	public static String getRongCloudToken(String uid, String name,
			String portraitUri) {
		String token = "";
		SdkHttpResult sdkHttpResult = null;
		try {
			sdkHttpResult = getToken(uid, name, portraitUri,
					FormatType.json);
			System.out.println("get rongCloud result from server:"+sdkHttpResult.toString());
			if (sdkHttpResult != null && sdkHttpResult.getHttpCode() == 200) {
				JSONObject jsonObject = JSON.parseObject(sdkHttpResult
						.getResult());
				token = jsonObject.getString("token");
			}
		} catch (Exception e) {
			logger.error("请求融云服务器获取token失败", e);
			return token;
		}
		return token;
	}

	// 获取token
	public static SdkHttpResult getToken(String userId, String userName,
			String portraitUri, FormatType format) throws Exception {
		if(Constant.DEVMODE){
			appKey = "25wehl3uwo82w";
			appSecret = "24k5q6qUN0ULxr";
		}else{
			appKey = "qd46yzrf4jeqf";
			appSecret = "s8k8ID07ppzQ";
		}
		
		HttpURLConnection conn = HttpUtil
				.CreatePostHttpConnection(appKey, appSecret, RONGCLOUDURI
						+ "/user/getToken." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		sb.append("&name=").append(
				URLEncoder.encode(userName == null ? "" : userName, UTF8));
		sb.append("&portraitUri=")
				.append(URLEncoder.encode(portraitUri == null ? ""
						: portraitUri, UTF8));
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 检查用户在线状态
	public static SdkHttpResult checkOnline(String userId, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/user/checkOnline." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 刷新用户信息
	public static SdkHttpResult refreshUser(String userId, String userName,
			String portraitUri, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/user/refresh." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		if (userName != null) {
			sb.append("&name=").append(URLEncoder.encode(userName, UTF8));
		}
		if (portraitUri != null) {
			sb.append("&portraitUri=").append(
					URLEncoder.encode(portraitUri, UTF8));
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 封禁用户
	public static SdkHttpResult blockUser(String userId, int minute,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/user/block." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		sb.append("&minute=").append(
				URLEncoder.encode(String.valueOf(minute), UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 解禁用户
	public static SdkHttpResult unblockUser(String userId, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/user/unblock." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 获取被封禁用户
	public static SdkHttpResult queryBlockUsers(FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/user/block/query." + format.toString());

		return HttpUtil.returnResult(conn);
	}

	// 添加用户到黑名单
	public static SdkHttpResult blackUser(String userId,
			List<String> blackUserIds, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/user/blacklist/add." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		if (blackUserIds != null) {
			for (String blackId : blackUserIds) {
				sb.append("&blackUserId=").append(
						URLEncoder.encode(blackId, UTF8));
			}
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 从黑名单移除用户
	public static SdkHttpResult unblackUser(String userId,
			List<String> blackUserIds, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/user/blacklist/remove." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		if (blackUserIds != null) {
			for (String blackId : blackUserIds) {
				sb.append("&blackUserId=").append(
						URLEncoder.encode(blackId, UTF8));
			}
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 获取黑名单用户
	public static SdkHttpResult QueryblackUser(String userId, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/user/blacklist/query." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 创建群
	public static SdkHttpResult createGroup(List<String> userIds,
			String groupId, String groupName, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/group/create." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(URLEncoder.encode(groupId, UTF8));
		sb.append("&groupName=").append(
				URLEncoder.encode(groupName == null ? "" : groupName, UTF8));
		if (userIds != null) {
			for (String id : userIds) {
				sb.append("&userId=").append(URLEncoder.encode(id, UTF8));
			}
		}
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 加入群
	public static SdkHttpResult joinGroup(String userId, String groupId,
			String groupName, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/group/join." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		sb.append("&groupId=").append(URLEncoder.encode(groupId, UTF8));
		sb.append("&groupName=").append(
				URLEncoder.encode(groupName == null ? "" : groupName, UTF8));
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 批量加入群
	public static SdkHttpResult joinGroupBatch(List<String> userIds,
			String groupId, String groupName, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/group/join." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(URLEncoder.encode(groupId, UTF8));
		sb.append("&groupName=").append(
				URLEncoder.encode(groupName == null ? "" : groupName, UTF8));
		if (userIds != null) {
			for (String id : userIds) {
				sb.append("&userId=").append(URLEncoder.encode(id, UTF8));
			}
		}
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 退出群
	public static SdkHttpResult quitGroup(String userId, String groupId,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/group/quit." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		sb.append("&groupId=").append(URLEncoder.encode(groupId, UTF8));
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 批量退出群
	public static SdkHttpResult quitGroupBatch(List<String> userIds,
			String groupId, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/group/quit." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(URLEncoder.encode(groupId, UTF8));
		if (userIds != null) {
			for (String id : userIds) {
				sb.append("&userId=").append(URLEncoder.encode(id, UTF8));
			}
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 解散群
	public static SdkHttpResult dismissGroup(String userId, String groupId,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil
				.CreatePostHttpConnection(appKey, appSecret, RONGCLOUDURI
						+ "/group/dismiss." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		sb.append("&groupId=").append(URLEncoder.encode(groupId, UTF8));
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 同步用户群信息
	public static SdkHttpResult syncGroup(String userId,
			List<GroupInfo> groups, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret, RONGCLOUDURI + "/group/sync." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
		if (groups != null) {
			for (GroupInfo info : groups) {
				if (info != null) {
					sb.append(
							String.format("&group[%s]=",
									URLEncoder.encode(info.getId(), UTF8)))
							.append(URLEncoder.encode(info.getName(), UTF8));
				}
			}
		}
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 刷新群信息
	public static SdkHttpResult refreshGroupInfo(String groupId,
			String groupName, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil
				.CreatePostHttpConnection(appKey, appSecret, RONGCLOUDURI
						+ "/group/refresh." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(URLEncoder.encode(groupId, UTF8));
		sb.append("&groupName=").append(
				URLEncoder.encode(groupName == null ? "" : groupName, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 刷新群信息
	public static SdkHttpResult refreshGroupInfo(GroupInfo group,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil
				.CreatePostHttpConnection(appKey, appSecret, RONGCLOUDURI
						+ "/group/refresh." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(URLEncoder.encode(group.getId(), UTF8));
		sb.append("&groupName=").append(
				URLEncoder.encode(group.getName(), UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 发送消息
	public static SdkHttpResult publishMessage(String fromUserId,
			List<String> toUserIds, Message msg, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/message/private/publish." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("fromUserId=").append(URLEncoder.encode(fromUserId, UTF8));
		if (toUserIds != null) {
			for (int i = 0; i < toUserIds.size(); i++) {
				sb.append("&toUserId=").append(
						URLEncoder.encode(toUserIds.get(i), UTF8));
			}
		}
		sb.append("&objectName=")
				.append(URLEncoder.encode(msg.getType(), UTF8));
		sb.append("&content=").append(URLEncoder.encode(msg.toString(), UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 发送消息
	public static SdkHttpResult publishMessage(String fromUserId,
			List<String> toUserIds, Message msg, String pushContent,
			String pushData, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/message/publish." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("fromUserId=").append(URLEncoder.encode(fromUserId, UTF8));
		if (toUserIds != null) {
			for (int i = 0; i < toUserIds.size(); i++) {
				sb.append("&toUserId=").append(
						URLEncoder.encode(toUserIds.get(i), UTF8));
			}
		}
		sb.append("&objectName=")
				.append(URLEncoder.encode(msg.getType(), UTF8));
		sb.append("&content=").append(URLEncoder.encode(msg.toString(), UTF8));

		if (pushContent != null) {
			sb.append("&pushContent=").append(
					URLEncoder.encode(pushContent == null ? "" : pushContent,
							UTF8));
		}

		if (pushData != null) {
			sb.append("&pushData=").append(
					URLEncoder.encode(pushData == null ? "" : pushData, UTF8));
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 发送系统消息
	public static SdkHttpResult publishSystemMessage(String fromUserId,
			List<String> toUserIds, Message msg, String pushContent,
			String pushData, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/message/system/publish." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("fromUserId=").append(URLEncoder.encode(fromUserId, UTF8));
		if (toUserIds != null) {
			for (int i = 0; i < toUserIds.size(); i++) {
				sb.append("&toUserId=").append(
						URLEncoder.encode(toUserIds.get(i), UTF8));
			}
		}
		sb.append("&objectName=")
				.append(URLEncoder.encode(msg.getType(), UTF8));
		sb.append("&content=").append(URLEncoder.encode(msg.toString(), UTF8));

		if (pushContent != null) {
			sb.append("&pushContent=").append(
					URLEncoder.encode(pushContent == null ? "" : pushContent,
							UTF8));
		}

		if (pushData != null) {
			sb.append("&pushData=").append(
					URLEncoder.encode(pushData == null ? "" : pushData, UTF8));
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 发送群消息
	public static SdkHttpResult publishGroupMessage(String fromUserId,
			List<String> toGroupIds, Message msg, String pushContent,
			String pushData, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/message/group/publish." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("fromUserId=").append(URLEncoder.encode(fromUserId, UTF8));
		if (toGroupIds != null) {
			for (int i = 0; i < toGroupIds.size(); i++) {
				sb.append("&toGroupId=").append(
						URLEncoder.encode(toGroupIds.get(i), UTF8));
			}
		}
		sb.append("&objectName=")
				.append(URLEncoder.encode(msg.getType(), UTF8));
		sb.append("&content=").append(URLEncoder.encode(msg.toString(), UTF8));

		if (pushContent != null) {
			sb.append("&pushContent=").append(
					URLEncoder.encode(pushContent == null ? "" : pushContent,
							UTF8));
		}

		if (pushData != null) {
			sb.append("&pushData=").append(
					URLEncoder.encode(pushData == null ? "" : pushData, UTF8));
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 发送聊天室消息
	public static SdkHttpResult publishChatroomMessage(String fromUserId,
			List<String> toChatroomIds, Message msg, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil
				.CreatePostHttpConnection(appKey, appSecret, RONGCLOUDURI
						+ "/message/chatroom/publish." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("fromUserId=").append(URLEncoder.encode(fromUserId, UTF8));
		if (toChatroomIds != null) {
			for (int i = 0; i < toChatroomIds.size(); i++) {
				sb.append("&toChatroomId=").append(
						URLEncoder.encode(toChatroomIds.get(i), UTF8));
			}
		}
		sb.append("&objectName=")
				.append(URLEncoder.encode(msg.getType(), UTF8));
		sb.append("&content=").append(URLEncoder.encode(msg.toString(), UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	public static SdkHttpResult broadcastMessage(String fromUserId,
			Message msg, String pushContent, String pushData, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/message/broadcast." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("fromUserId=").append(URLEncoder.encode(fromUserId, UTF8));
		sb.append("&objectName=")
				.append(URLEncoder.encode(msg.getType(), UTF8));
		sb.append("&content=").append(URLEncoder.encode(msg.toString(), UTF8));
		if (pushContent != null) {
			sb.append("&pushContent=").append(
					URLEncoder.encode(pushContent == null ? "" : pushContent,
							UTF8));
		}

		if (pushData != null) {
			sb.append("&pushData=").append(
					URLEncoder.encode(pushData == null ? "" : pushData, UTF8));
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 创建聊天室
	public static SdkHttpResult createChatroom(List<ChatroomInfo> chatrooms,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/chatroom/create." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("1=1");
		if (chatrooms != null) {
			for (ChatroomInfo info : chatrooms) {
				if (info != null) {
					sb.append(
							String.format("&chatroom[%s]=",
									URLEncoder.encode(info.getId(), UTF8)))
							.append(URLEncoder.encode(info.getName(), UTF8));
				}
			}
		}
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 销毁聊天室
	public static SdkHttpResult destroyChatroom(List<String> chatroomIds,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/chatroom/destroy." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("1=1");
		if (chatroomIds != null) {
			for (String id : chatroomIds) {
				sb.append("&chatroomId=").append(URLEncoder.encode(id, UTF8));
			}
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 查询聊天室信息
	public static SdkHttpResult queryChatroom(List<String> chatroomIds,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/chatroom/query." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("1=1");
		if (chatroomIds != null) {
			for (String id : chatroomIds) {
				sb.append("&chatroomId=").append(URLEncoder.encode(id, UTF8));
			}
		}

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 获取消息历史记录下载地址
	public static SdkHttpResult getMessageHistoryUrl(String date,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/message/history." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("date=").append(URLEncoder.encode(date, UTF8));
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 删除消息历史记录
	public static SdkHttpResult deleteMessageHistory(String date,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/message/history/delete." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("date=").append(URLEncoder.encode(date, UTF8));
		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	// 获取群内成员
	public static SdkHttpResult queryGroupUserList(String groupId,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/group/user/query." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(
				URLEncoder.encode(groupId == null ? "" : groupId, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	public static SdkHttpResult groupUserGagAdd(String groupId, String userId,
			long minute, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/group/user/gag/add." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(
				URLEncoder.encode(groupId == null ? "" : groupId, UTF8));
		sb.append("userId=").append(
				URLEncoder.encode(userId == null ? "" : userId, UTF8));
		sb.append("minute=").append(
				URLEncoder.encode(String.valueOf(minute), UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	public static SdkHttpResult groupUserGagRollback(String groupId,
			String userId, FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/group/user/gag/rollback." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(
				URLEncoder.encode(groupId == null ? "" : groupId, UTF8));
		sb.append("userId=").append(
				URLEncoder.encode(userId == null ? "" : userId, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	public static SdkHttpResult groupUserGagList(String groupId,
			FormatType format) throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/group/user/gag/list." + format.toString());

		StringBuilder sb = new StringBuilder();
		sb.append("groupId=").append(
				URLEncoder.encode(groupId == null ? "" : groupId, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	public static SdkHttpResult wordFilterAdd(String word, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/wordfilter/add." + format.toString());

		if (word == null || word.length() == 0) {
			throw new Exception("word is not null or empty.");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("word=").append(
				URLEncoder.encode(word == null ? "" : word, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	public static SdkHttpResult wordFilterDelete(String word, FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/wordfilter/delete." + format.toString());

		if (word == null || word.length() == 0) {
			throw new Exception("word is not null or empty.");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("word=").append(
				URLEncoder.encode(word == null ? "" : word, UTF8));

		HttpUtil.setBodyParameter(sb, conn);

		return HttpUtil.returnResult(conn);
	}

	public static SdkHttpResult wordFilterList(FormatType format)
			throws Exception {

		HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
				appSecret,
				RONGCLOUDURI + "/wordfilter/delete." + format.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("1=1");
		HttpUtil.setBodyParameter(sb, conn);
		return HttpUtil.returnResult(conn);
	}
}
