package com.syz.mgr;

import java.util.List;
import java.util.Map;

import com.syz.mybatis.crud.Page;
import com.syz.service.bean.SysTest;

public interface SysTestMgr {

	void saveSysTest(Map<String, Object> daoArgs);

	List<SysTest> querySysTest(Map<String, Object> daoArgs);

	Page<SysTest> queryPageSysTest(Map<String, Object> daoArgs);

}
