package com.shanjin.manager.intercepter;

import java.lang.reflect.Method; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Map; 
import java.util.Set; 
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation; 
import com.jfinal.core.Controller; 
public class AutoInjectInterceptor implements Interceptor { 
	private static Object[] NULL = null;
	private static final String GET = "get"; 
	private static final String SET = "set"; 
	public void intercept(ActionInvocation ai) { 
		Controller controller = ai.getController(); 
		Map<String,String[]> paraMap = controller.getParaMap(); 
		Set<String> nameSet = paraMap.keySet();   
		try {           
			for(String name:nameSet){  
			inject(controller, name, paraMap.get(name));   
			}       
			} catch (Exception e) {           
				e.printStackTrace(); 
				}       
		ai.invoke();   
		}        
	private void inject(Object obj,String prop,String[] values) throws Exception{ 
		String[] props = prop.split("\\.");    
		if(props.length == 1){                 
			Class<?> type = obj.getClass().getDeclaredField(prop).getType();  
			Method set = obj.getClass().getMethod(toMethodName(SET, prop), type);     
			if(type == String.class){            
				set.invoke(obj, values[0]);    
				}else if(type == Integer.class){
					set.invoke(obj, Integer.parseInt(values[0]));   
					}else if(type == Long.class){             
						set.invoke(obj, Long.parseLong(values[0]));   
						}else if(type == Date.class){    
							set.invoke(obj, new SimpleDateFormat("YYYY/HH/DD").parse(values[0])); 
							}else if(type == Float.class){ 
								set.invoke(obj, Float.parseFloat(values[0])); 
								}else{
									set = obj.getClass().getMethod(toMethodName(SET, prop), String.class);
									set.invoke(obj, values[0]);   
									}      
			}else{      
				Method get = obj.getClass().getMethod(toMethodName(GET, props[0]));    
				Method set = obj.getClass().getMethod(toMethodName(SET, props[0]), get.getReturnType());   
				Object instance =  get.invoke(obj, NULL);      
				if(instance == null){              
					instance = get.getReturnType().newInstance();   
					}            
				inject(instance, prop.replaceFirst(props[0] + ".", ""), values);
				set.invoke(obj, instance);       
				}     }    
	private String toMethodName(String prefix,String name){ 
		return prefix + name.substring(0, 1).toUpperCase() + name.substring(1,name.length()); 
		}
	 } 



