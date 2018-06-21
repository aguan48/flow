package com.sinog2c.flow.util;

import java.io.Serializable;

public class Result implements Serializable {
	
	private static final long serialVersionUID = -852862475903031245L;

	protected Boolean success;
	
	protected String message;
	
	protected Object obj;
	
	public Result(){}
	
	public Result(Boolean success, String message){
		this.success = success;
		this.message = message;
	}
	
	public Result(Boolean success, String message, Object obj){
		this.success = success;
		this.message = message;
		this.obj = obj;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
}
