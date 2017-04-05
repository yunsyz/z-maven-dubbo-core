package com.syz.service;

import com.syz.service.msg.ST001Req;
import com.syz.service.msg.ST001Resp;
import com.syz.service.msg.ST002Req;
import com.syz.service.msg.ST002Resp;
import com.syz.service.msg.ST003Req;
import com.syz.service.msg.ST003Resp;
/**
 * 测试接口
 * @author yunsyz
 *
 */
public interface SysTestService {
	/**
	 * 增加
	 * 
	 * @param req
	 * @return
	 */
	ST001Resp st001(ST001Req req);

	/**
	 * 查询
	 * 
	 * @param req
	 * @return
	 */
	ST002Resp st002(ST002Req req);

	/**
	 * 分页查询
	 * 
	 * @param req
	 * @return
	 */
	ST003Resp st003(ST003Req req);
}
