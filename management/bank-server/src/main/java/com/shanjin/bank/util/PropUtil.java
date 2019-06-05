package com.shanjin.bank.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropUtil    
{   
       
    public static Properties getPropUtil (String fileName) {   
           
        Properties config = new Properties();   
        InputStream is = null;   
        try {   
        	config.load(new InputStreamReader(PropUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));   
        } catch (IOException e) {   
               
        } finally {
            if (is != null) {   
                try {   
                    is.close();   
                } catch (IOException e) {   
                }   
           }   
        }   
       return config;   
   }

	
}