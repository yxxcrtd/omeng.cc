package com.shanjin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.shanjin.dao.IOrderInfoDao;
import com.shanjin.service.IOrderInfoService;

@Service("orderInfoService")
public class OrderInfoServiceImpl implements IOrderInfoService {

	@Resource
	private IOrderInfoDao iOrderInfoDao;
	
	@Override
	public Map<String, Object> getOrderNote(Map<String, Object> paramMap) {
		Map<String, Object> orderNote = new HashMap<String, Object>();
    	List<Map<String, Object>> attachments = this.iOrderInfoDao.selectOrderAttachment(paramMap);
    	if(attachments != null && attachments.size() > 0) {
    		Map<String, String> temp = new HashMap<String, String>(); 
    		for(Map<String, Object> attachment : attachments) {
    			Integer type = (Integer)attachment.get("type");
    			if(type == 1){
    				String picturePath = temp.get("picturePath");
    				if(picturePath == null) {
    					temp.put("picturePath", (String)attachment.get("path"));
    				} else {
    					StringBuilder str=new StringBuilder();
    					str.append(picturePath).append(",").append((String)attachment.get("path"));
    					temp.put("picturePath", str.toString());
    				}
    			} else if (type == 2) {
    				String voicePath = temp.get("voicePath");
    				if(voicePath == null) {
    					temp.put("voicePath", (String)attachment.get("path"));
    				} else {
    					StringBuilder str=new StringBuilder();
    					str.append(voicePath).append(",").append((String)attachment.get("path"));
    					temp.put("voicePath", str.toString());
    				}
    			}
    		}
    		
    		orderNote.putAll(temp);
    	}
    	return orderNote;
	}

}
