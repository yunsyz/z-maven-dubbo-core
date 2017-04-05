package com.syz.init;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import com.syz.init.bean.MethodBean;

/**
 * 反射工具类，扫描所有的指定路径下的service及其方法
 * 
 * @ClassName: MethodToClassManager
 * @Description: TODO
 * @author: sunyz
 * @date: 2017年1月17日 上午10:54:58
 */
public class MethodToClassManager implements InitializingBean {
	private static Logger logger = Logger.getLogger(MethodToClassManager.class);

	private static MethodToClassManager global = null;

	/**
	 * 定义classMap
	 */
	private Map<String, MethodBean> classMap = new HashMap<String, MethodBean>();

	/**
	 * 定义扫描包
	 */
	private String[] packageNames = { "com.syz.service" };

	/**
	 * 排除类，如果写包名，将排除此包下所有类
	 */
	private String[] excludes = {};

	public static MethodToClassManager getGlobal() {
		return global;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		global = this;
		this.init();
	}

	public Map<String, MethodBean> getClassMap() {
		return classMap;
	}

	public void setClassMap(Map<String, MethodBean> classMap) {
		this.classMap = classMap;
	}

	public MethodBean getMethodBean(String methodName) {
		return classMap.get(methodName);
	}

	public void init() {
		load();
	}

	/**
	 * 初始化，扫描路径下所有的接口类，并将方法放入methodBean中，再放入classMap中
	 * 
	 * @Title: load
	 * @Description: TODO
	 * @return: void
	 */
	private void load() {
		List<String> clazzNameList = convert(packageNames, excludes);
		try {
			for (String clazzName : clazzNameList) {
				Class<?> clazz = Class.forName(clazzName);
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					String methodName = method.getName();
					methodName = methodName.toUpperCase();
					Class<?>[] inClazzs = method.getParameterTypes();
					for (int i = 0; i < inClazzs.length; i++) {
						Class<?> inClazz = inClazzs[i];
						MethodBean methodBean = new MethodBean();
						methodBean.setClassName(clazzName);
						methodBean.setParamClassName(inClazz.getName());
						classMap.put(methodName, methodBean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(classMap.toString());
	}

	public List<String> convert(String packageName, String[] excludes) {
		String[] packageNames = { packageName };
		return convert(packageNames, excludes);
	}

	public List<String> convert(String[] packageNames, String[] excludes) {
		List<String> list = new ArrayList<>();
		try {
			for (String packageName : packageNames) {
				String config = "classpath:/" + packageName.replace(".", "/") + "/**/*Service.class";
				PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
				MetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);
				Resource[] resources = resolver.getResources(config);
				for (Resource resource : resources) {
					MetadataReader reader = factory.getMetadataReader(resource);
					ClassMetadata data = reader.getClassMetadata();
					boolean flag = data.isInterface();
					if (flag) {
						String clazzName = data.getClassName();
						boolean exclude = checkExcludes(clazzName, excludes);
						if (exclude) {
							continue;
						}
						list.add(clazzName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean checkExcludes(String name, String[] excludes) {
		if (excludes == null) {
			return false;
		}
		for (int i = 0; i < excludes.length; i++) {
			if (excludes[i] == null || excludes[i].length() < 1) {
				continue;
			}
			if (name.indexOf(excludes[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		MethodToClassManager manager = new MethodToClassManager();
		manager.load();
	}

}