package com.syz.service.msg;

import java.util.List;

import com.syz.base.msg.BaseResponseMsg;
import com.syz.service.bean.SysTest;

/**
 * @ClassName: ST002
 * @Description: 查询 返回参数
 * @author: syz
 * @date: 2017-03-25 17:01:51
 */
public class ST002Resp extends BaseResponseMsg {

    private static final long serialVersionUID = 1L;
	private List<SysTest> list;

	public List<SysTest> getList() {
		return list;
	}

	public void setList(List<SysTest> list) {
		this.list = list;
	}

}