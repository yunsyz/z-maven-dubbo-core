package com.syz.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static ConfigurableListableBeanFactory beanFactory; // Spring应用上下文环境

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		MyBeanFactoryPostProcessor.beanFactory = beanFactory;
	}

	public static <T> T getBean(Class<T> clazz) throws BeansException {
		T result = (T) beanFactory.getBean(clazz);
		return result;
	}

}