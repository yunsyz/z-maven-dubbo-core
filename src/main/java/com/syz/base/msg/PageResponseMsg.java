package com.syz.base.msg;

/**
 * 分页出参
 * 
 * @ClassName: PageResponseMsg
 * @Description: TODO
 * @author: sunyz
 * @date: 2017年1月17日 下午2:19:34
 */
public class PageResponseMsg extends BaseResponseMsg {
	private static final long serialVersionUID = 1L;

	/**
	 * 总记录数
	 */
	private int total;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}