package com.shanjin.manager.utils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
public class Test {
public static String getFile(String str) {
	String o1="'";
	return str.replace(o1, "\\'");
}

public static void main(String[] args) throws ParseException{
	Map<String,Object> map=new HashMap<String,Object>();
	map.put("a", 1);
	map.put("b", 2);
	map.put("c", 3);
	map.put("d", 4);
	map.put("e", 5);
	map.put("f", 6);
	
	System.out.println(map.get("a"));
	System.out.println(map.size());
	
	map.remove("c");
	System.out.println(map.get("c"));
	System.out.println(map.size());
	
}
}
