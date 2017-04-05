package com.syz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.syz.base.BaseService;
import com.syz.mgr.SysTestMgr;
import com.syz.mybatis.crud.Page;
import com.syz.service.SysTestService;
import com.syz.service.bean.SysTest;
import com.syz.service.msg.ST001Req;
import com.syz.service.msg.ST001Resp;
import com.syz.service.msg.ST002Req;
import com.syz.service.msg.ST002Resp;
import com.syz.service.msg.ST003Req;
import com.syz.service.msg.ST003Resp;

@Service
public class SysTestServiceImpl extends BaseService implements SysTestService {
	@Resource
	private SysTestMgr sysTestMgr;
	@Override
	public ST001Resp st001(ST001Req req) {
		ST001Resp resp = new ST001Resp();
		TransactionStatus txstatus = null;
		try {
			// 参数合法性校验
			req.check();
			// 业务逻辑
			Map<String, Object> daoArgs = new HashMap<String, Object>();
			daoArgs.put("id", req.getId());
			daoArgs.put("name", req.getName());
			// 开启事务
			txstatus = this.txBegin(0);
			this.sysTestMgr.saveSysTest(daoArgs);
			// 提交事务
			this.txCommit(txstatus);
			txstatus = null;
			resp.setRetCode("0");
			resp.setRetMsg("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (txstatus != null) {
				this.txRollback(txstatus);
			}
		}
		return resp;
	}

	@Override
	public ST002Resp st002(ST002Req req) {
		ST002Resp resp = new ST002Resp();
		try {
			// 参数合法性校验
			req.check();
			// 业务逻辑
			Map<String, Object> daoArgs = new HashMap<String, Object>();
			daoArgs.put("name", req.getName());
			List<SysTest> list = this.sysTestMgr.querySysTest(daoArgs);
			resp.setList(list);
			resp.setRetCode("0");
			resp.setRetMsg("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public ST003Resp st003(ST003Req req) {
		ST003Resp resp = new ST003Resp();
		try {
			// 参数合法性校验
			req.check();
			// 业务逻辑
			Map<String, Object> daoArgs = new HashMap<String, Object>();
			daoArgs.put("name", req.getName());
			daoArgs.put("pageSize", req.getPageSize());
			daoArgs.put("pageNo", req.getPageNo());
			Page<SysTest> page = this.sysTestMgr.queryPageSysTest(daoArgs);
			resp.setTotal(page.getTotal());
			resp.setList(page.getResult());
			resp.setRetCode("0");
			resp.setRetMsg("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}


}
