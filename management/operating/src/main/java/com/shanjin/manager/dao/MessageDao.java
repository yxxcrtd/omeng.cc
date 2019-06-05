package com.shanjin.manager.dao;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.Message;

public interface MessageDao {
	
	public List<Message> getMessageList(Map<String, String[]> param);
	
	public boolean saveMessage(Map<String, String[]> param);
	
	public boolean deleteMessage(String ids);
	
	public boolean sendMessage(String id);
}
