package com.syz.mybatis.crud;

import java.sql.Connection;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;


public class MyBatisDaoImpl extends SqlSessionDaoSupport implements MyBatisDao {
	public void setSuperSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public <T> List<T> query(String statementName, Object parameterObject) {
		try {
			return getSqlSession().selectList(statementName, parameterObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Page<T> query(String statementName, Object parameterObject, int pagesize, int pageno) {
		RowBounds rbs = null;

		Page page = new Page();
		try {
			List result;
			if (pagesize > 0) {
				if (pageno < 1)
					pageno = 1;

				int start = (pageno - 1) * pagesize + 1;
				int end = start + pagesize - 1;
				rbs = new RowBounds(start, end);
				result = getSqlSession().selectList(statementName, parameterObject, rbs);
				page.setTotal(PageInterceptor.getTotalRowCount());
				page.setBeginnum(start);
			} else {
				result = getSqlSession().selectList(statementName, parameterObject);
				page.setTotal(result.size());
				page.setBeginnum(1);
			}
			page.setResult(result);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}

	public int insert(String statementName, Object parameterObject) {
		try {
			return getSqlSession().insert(statementName, parameterObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}

	public int update(String statementName, Object parameterObject) {
		try {
			return getSqlSession().update(statementName, parameterObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}

	public int delete(String statementName, Object parameterObject) {
		try {
			return getSqlSession().delete(statementName, parameterObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}

	public <T> T excuteForSql(String statementName, Object parameterObject) {
		try {
			return getSqlSession().selectOne(statementName, parameterObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}

	public Connection getConn() {
		try {
			return getSqlSession().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}
}