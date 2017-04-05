package com.syz.base.msg;

/**
 * 分页入参
 * 
 * @ClassName: PageRequestMsg
 * @Description: TODO
 * @author: sunyz
 * @date: 2017年1月17日 下午2:17:15
 */
public class PageRequestMsg extends BaseRequestMsg {
	private static final long serialVersionUID = 1L;

	/**
	 * 第几页
	 */
	private int pageNo;

	/**
	 * 分页大小
	 */
	private int pageSize;

	public PageRequestMsg() {
		this.pageNo = 1;
		this.pageSize = 10;
	}

	@Override
	public void check() throws Exception {
		super.check();
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}