package com.syz.init.bean;

public class MethodBean {
	private String className;

	private String paramClassName;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParamClassName() {
		return paramClassName;
	}

	public void setParamClassName(String paramClassName) {
		this.paramClassName = paramClassName;
	}

	@Override
	public String toString() {
		return "{\"className\":\"" + this.className + "\",\"paramClassName\":\"" + this.paramClassName + "\"}";
	}

}