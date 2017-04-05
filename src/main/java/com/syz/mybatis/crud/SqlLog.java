package com.syz.mybatis.crud;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

@Intercepts({ @org.apache.ibatis.plugin.Signature(type = StatementHandler.class, method = "prepare", args = {
		java.sql.Connection.class }) })
public class SqlLog implements Interceptor {
	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
				DEFAULT_OBJECT_WRAPPER_FACTORY);
		MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
		BoundSql boundSql = statementHandler.getBoundSql();

		String sqlId = mappedStatement.getId();
		if (!(sqlId.equals("JpdAppExceptionLogMapper.insertSelective"))) {
			Configuration configuration = mappedStatement.getConfiguration();
			String showSql = getSql(configuration, boundSql, sqlId);
			System.out.println(showSql);
		}

		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties arg0) {
	}

	public static String showLogSql(Configuration configuration, BoundSql boundSql) {
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		if ((parameterMappings.size() > 0) && (parameterObject != null)) {
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = sql.replaceFirst("\\?", filterDollarStr(getParameterValue(parameterObject)));
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				for (ParameterMapping parameterMapping : parameterMappings) {
					String propertyName = parameterMapping.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", filterDollarStr(getParameterValue(obj)));
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", filterDollarStr(getParameterValue(obj)));
					}
				}
			}
		}
		return sql;
	}

	public static String filterDollarStr(String str) {
		String sReturn = "";
		if (str.trim().length() != 0) {
			if (str.indexOf("$", 0) > -1) {
				while (str.length() > 0)
					if (str.indexOf("$", 0) > -1) {
						sReturn = sReturn + str.subSequence(0, str.indexOf("$", 0));
						sReturn = sReturn + "\\$";
						str = str.substring(str.indexOf("$", 0) + 1, str.length());
					} else {
						sReturn = sReturn + str;
						str = "";
					}
			} else {
				sReturn = str;
			}
		}
		return sReturn;
	}

	public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
		String sql = showLogSql(configuration, boundSql);
		StringBuilder str = new StringBuilder(100);
		str.append("===========================================");
		str.append("[");
		str.append(sqlId);
		str.append("]");
		str.append("===========================================");
		str.append("\n");
		str.append(sql);
		str.append("\n");
		return str.toString();
	}

	private static String getParameterValue(Object obj) {
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
}