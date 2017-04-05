package com.syz.mybatis.crud;

import java.sql.Connection;
import java.util.List;

public abstract interface MyBatisDao {
	public abstract <T> List<T> query(String statementName, Object paramObject);

	public abstract <T> Page<T> query(String statementName, Object paramObject, int pageSize, int pageNo);

	public abstract int insert(String statementName, Object paramObject);

	public abstract int update(String statementName, Object paramObject);

	public abstract int delete(String statementName, Object paramObject);

	public abstract <T> T excuteForSql(String statementName, Object paramObject);

	public abstract Connection getConn();
}