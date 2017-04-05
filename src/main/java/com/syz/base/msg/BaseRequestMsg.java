package com.syz.base.msg;

import java.io.Serializable;

/**
 * 入参基类
 * 
 * @ClassName: BaseRequestMsg
 * @Description: TODO
 * @author: sunyz
 * @date: 2017年1月17日 下午2:12:50
 */
public class BaseRequestMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 请求token
	 */
	protected String accessToken;

	/**
	 * 用户ID
	 */
	protected Integer userId;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * 字段检查
	 * 
	 * @Title: check
	 * @Description: TODO
	 * @throws Exception
	 * @return: void
	 */
	public void check() throws Exception {
		// userId不能为空
	}

}
