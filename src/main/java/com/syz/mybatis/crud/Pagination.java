package com.syz.mybatis.crud;

import java.io.Serializable;

public class Pagination implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 925555366872378913L;

	private int pageSize = 10;

	private int pageIndex = 0;

	private int totalRecord = 0;
	private int totalPage;

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getTotalRecord() {
		return this.totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
		this.totalPage = ((totalRecord + this.pageSize - 1) / this.pageSize);
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public void next() {
		this.pageIndex += 1;
	}

	public void prev() {
		this.pageIndex -= 1;
	}

	public void first() {
		this.pageIndex = 0;
	}

	public void last() {
		this.pageIndex = (this.totalPage - 1);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("pageSize:" + this.pageSize);
		sb.append(",pageIndex:" + this.pageIndex);
		sb.append(",totalRecord:" + this.totalRecord);
		sb.append(",totalPage:" + this.totalPage);
		return sb.toString();
	}
}