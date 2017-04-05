package com.syz.service.msg;

import com.syz.base.msg.BaseRequestMsg;

/**
 * @ClassName: ST002
 * @Description: 查询 请求参数
 * @author: syz
 * @date: 2017-03-25 17:01:51
 */
public class ST002Req extends BaseRequestMsg {

    private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void check() throws Exception {
		// TODO Auto-generated method stub
	}

}