package com.syz.mybatis.crud;

public class DaoException extends RuntimeException {
	private static final long serialVersionUID = -323085892363928032L;
	protected String errorCode;
	protected String errorMessage;
	protected Object[] params;

	public DaoException(String message, Exception ex) {
		super(message, ex);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Exception ex) {
		super(ex);
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Object[] getParams() {
		return this.params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}
}