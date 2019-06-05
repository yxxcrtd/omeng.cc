package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface IServiceTagDao {
	
	//获取系统标签前25个标签
    List<Map<String, Object>> getSystemTags();
    
    
    //获取个性化标签钱
    List<Map<String,Object>> getCustomTags();
      
    
    //查询用户自定意义服务名称是否存在
    int  existsServiceType(Map<String,Object> param);
    
    
    //增加自定义服务
    int  addCustomServiceType(Map<String,Object> param);
}
