package com.sinog2c.flow.util;

import java.io.Serializable;

public class DataJsonResult extends Result implements Serializable {
	
	private static final long serialVersionUID = -5130043419241774219L;

	private Object rows;//包含实际数据的数组
	
	private Long total; //总记录数
	
	public DataJsonResult() {}
	
	public DataJsonResult(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public Object getRows() {
		return rows;
	}

	public void setRows(Object rows) {
		this.rows = rows;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}
