package com.shanjin.exception;

public class ApplicationException extends RuntimeException {
	
		private String result; //返回码
	
		private String msg; //返回码对应的消息

		
		public ApplicationException(Exception e){
			 super(e);
		}
		
		public ApplicationException(Exception e,String result,String msg){
			 super(e);
			 this.result = result;
			 this.msg = msg;
		}
		
		public ApplicationException(String result,String msg){
			 super();
			 this.result = result;
			 this.msg = msg;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
		
		
		

}
