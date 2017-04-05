package com.syz.service.msg;

import com.syz.base.msg.BaseRequestMsg;

/**
 * @ClassName: ST001
 * @Description: 增加 请求参数
 * @author: syz
 * @date: 2017-03-25 17:01:51
 */
public class ST001Req extends BaseRequestMsg {

    private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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