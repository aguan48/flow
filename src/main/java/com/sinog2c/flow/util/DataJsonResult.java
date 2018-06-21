package com.sinog2c.flow.util;

import java.io.Serializable;
import java.util.List;

public class DataJsonResult extends Result implements Serializable {
	
	private static final long serialVersionUID = -5130043419241774219L;

	private Object rows;//包含实际数据的数组
	
	private Integer page;//当前页
	
	private Integer total;//总页数
	
	private Long records; //总记录数
	
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

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Long getRecords() {
		return records;
	}

	public void setRecords(Long records) {
		this.records = records;
	}
}
