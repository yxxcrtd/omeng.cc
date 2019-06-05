package com.shanjin.manager.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropUtil    
{   
       
    public static Properties getPropUtil (String fileName) {   
           
        Properties config = new Properties();   
        InputStream is = null;   
        try {   
            is = PropUtil .class.getClassLoader().getResourceAsStream(fileName);   
           config.load(is);   
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