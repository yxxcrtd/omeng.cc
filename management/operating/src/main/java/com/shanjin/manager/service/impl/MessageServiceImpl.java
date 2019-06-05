package com.shanjin.manager.service.impl;

import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.Message;
import com.shanjin.manager.dao.MessageDao;
import com.shanjin.manager.dao.impl.MessageDaoImpl;
import com.shanjin.manager.service.IMessageService;

public class MessageServiceImpl implements IMessageService{
	
	private MessageDao messageDao = new MessageDaoImpl();

	@Override
	public List<Message> getMessageList(Map<String, String[]> param) {
		return messageDao.getMessageList(param);
	}

	@Override
	public boolean saveMessage(Map<String, String[]> param) {
		return messageDao.saveMessage(param);
	}

	@Override
	public boolean deleteMessage(String ids) {
		return messageDao.deleteMessage(ids);
	}

	@Override
	public boolean sendMessage(String id) {
		return messageDao.sendMessage(id);
	}

}
