package com.syz.base;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.syz.mybatis.crud.MyBatisDao;

@Component
public class BaseMgr {
	@Resource(name = "dao")
	protected MyBatisDao dao;
}
