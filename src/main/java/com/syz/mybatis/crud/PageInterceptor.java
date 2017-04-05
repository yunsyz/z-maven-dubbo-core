package com.syz.mybatis.crud;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.log4j.Logger;


@Intercepts({ @org.apache.ibatis.plugin.Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class, org.apache.ibatis.session.ResultHandler.class }) })
public class PageInterceptor implements Interceptor {
	@SuppressWarnings("unused")
	private static final String MAPPED_STATEMENT = "mappedStatement";
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(PageInterceptor.class);

	private static ThreadLocal<Integer> totalRowCountHolder = new ThreadLocal<>();

	@SuppressWarnings("unused")
	public static void getCount(String sql, Connection connection) throws Throwable {
		PreparedStatement countStmt = null;
		ResultSet rs = null;

		Exception excpt = null;
		int count = 0;
		long t1 = new Date().getTime();
		try {
			countStmt = connection.prepareStatement(sql);
			rs = countStmt.executeQuery();
			count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}
			totalRowCountHolder.set(Integer.valueOf(count));
		} catch (Exception e) {
			excpt = e;
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (countStmt != null) {
				countStmt.close();
			}
		}
	}

	public static int getTotalRowCount() {
		return ((Integer) totalRowCountHolder.get()).intValue();
	}

	@SuppressWarnings("unused")
	public Object intercept(Invocation invocation) throws Throwable {
		Object returnValue = null;
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = null;
		RowBounds rowBounds = null;
		if (invocation.getArgs().length > 1) {
			parameter = invocation.getArgs()[1];
		}
		String sqlId = mappedStatement.getId();
		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		Configuration configuration = mappedStatement.getConfiguration();
		long time = System.currentTimeMillis();
		if (invocation.getArgs().length > 2) {
			rowBounds = (RowBounds) invocation.getArgs()[2];
			if ((rowBounds != null) && (rowBounds.getLimit() > 0) && (rowBounds.getLimit() < 2147483647)) {
				if ((boundSql == null) || (boundSql.getSql() == null) || ("".equals(boundSql.getSql())))
					return null;
				Executor exe = (Executor) invocation.getTarget();
				Connection connection = exe.getTransaction().getConnection();
				DatabaseMetaData dbMeta = connection.getMetaData();
				try {
					getCount(DataBaseDialectUtils.dbCountSql(dbMeta, mergeSQL(configuration, boundSql)), connection);

					int totalCount = getTotalRowCount();
					if (rowBounds.getLimit() > totalCount)
						rowBounds = new RowBounds(rowBounds.getOffset(), totalCount);
				} catch (Throwable e) {
					throw new SQLException(e);
				}
				String pSql = DataBaseDialectUtils.dbPageSql(dbMeta, boundSql.getSql(), rowBounds.getOffset(),
						rowBounds.getLimit());
				ReflectUtil.setFieldValue(boundSql, "sql", pSql);
				invocation.getArgs()[2] = new RowBounds(0, 2147483647);
				MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(boundSql));
				invocation.getArgs()[0] = newMs;
			}
		}

		returnValue = invocation.proceed();
		return returnValue;
	}

	private String mergeSQL(Configuration configuration, BoundSql boundSql) {
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		if ((parameterMappings.size() > 0) && (parameterObject != null)) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					}
				}
			}
		}
		return sql;
	}

	private String getParameterValue(Object obj) {
		String value = null;
		if (obj instanceof String) {
			value = "'" + obj.toString() + "'";
		} else if (obj instanceof Date) {
			DateFormat formatter = DateFormat.getDateTimeInstance(2, 2, Locale.CHINA);
			value = "'" + formatter.format(new Date()) + "'";
		} else if (obj != null) {
			value = obj.toString();
		} else {
			value = "";
		}

		return value;
	}

	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if ((ms.getKeyProperties() != null) && (ms.getKeyProperties().length > 0)) {
			builder.keyProperty(ms.getKeyProperties()[0]);
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.cache(ms.getCache());
		MappedStatement newMs = builder.build();
		return newMs;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties arg0) {
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return this.boundSql;
		}
	}
}