package com.shanjin.manager.constant;

public class ResultInfo {
    private String code;
    private String message;
    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public String Code(){
        return code;
    }
    public String Message(){
        return message;
    }
    public ResultInfo() {
        
    }
    public ResultInfo(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
    
}
