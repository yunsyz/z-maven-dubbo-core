package com.syz.base.msg;

import java.io.Serializable;

/**
 * 出参基类
 * 
 * @ClassName: BaseResponseMsg
 * @Description: TODO
 * @author: sunyz
 * @date: 2017年1月17日 下午2:15:35
 */
public abstract class BaseResponseMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	public BaseResponseMsg() {

	}

	public BaseResponseMsg(String retCode, String retMsg) {
		super();
		this.retCode = retCode;
		this.retMsg = retMsg;
	}

	/**
	 * 错误代码
	 */
	protected String retCode;

	/**
	 * 返回信息
	 */
	protected String retMsg;

	/**
	 * 异常堆栈信息
	 */
	protected String errMsg;

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

}
