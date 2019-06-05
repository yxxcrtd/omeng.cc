package com.shanjin.manager.service;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.Message;

public interface IMessageService {
	
	public List<Message> getMessageList(Map<String, String[]> param);
	
	public boolean saveMessage(Map<String, String[]> param);
	
	public boolean deleteMessage(String ids);
	
	public boolean sendMessage(String id);
	
}
