package com.syz.mybatis.crud;

import java.sql.DatabaseMetaData;

public class DataBaseDialectUtils {
	public static String dbPageSql(DatabaseMetaData dbMeta, String sql, int skipResults, int maxResults)
			throws Exception {
		String dbName = dbMeta.getDatabaseProductName();
		if ("MySQL".equals(dbName)) {
			String pageSql = sql + " limit " + (skipResults - 1) + " , " + (maxResults - (skipResults - 1));
			return pageSql;
		}

		if ("Oracle".equals(dbName)) {
			String pageSql = "SELECT * FROM (SELECT page.*, ROWNUM AS rn FROM (" + sql + ") page) WHERE rn BETWEEN "
					+ skipResults + " AND " + maxResults;
			return pageSql;
		}
		return null;
	}

	public static String dbCountSql(DatabaseMetaData dbMeta, String sql) throws Exception {
		String dbName = dbMeta.getDatabaseProductName();
		if ("Oracle".equals(dbName)) {
			return "SELECT count(1) FROM (" + sql + ")";
		}
		return "select count(*) from (" + sql + ") as total";
	}
}