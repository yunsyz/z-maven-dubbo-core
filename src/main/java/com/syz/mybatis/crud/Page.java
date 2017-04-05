package com.syz.mybatis.crud;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8500865114042628664L;
	int total;
	int beginnum;
	private List<T> result;

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getBeginnum() {
		return this.beginnum;
	}

	public void setBeginnum(int beginnum) {
		this.beginnum = beginnum;
	}

	public void setResult(List<T> elements) {
		if (elements == null)
			throw new IllegalArgumentException("'result' must be not null");
		this.result = elements;
	}

	public List<T> getResult() {
		return this.result;
	}
}