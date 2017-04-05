package com.syz.dubbo.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syz.base.MyBeanFactoryPostProcessor;
import com.syz.base.msg.BaseRequestMsg;
import com.syz.base.msg.BaseResponseMsg;
import com.syz.dubbo.DubboBaseService;
import com.syz.dubbo.bean.DubboInParamBean;
import com.syz.dubbo.bean.DubboOutParamBean;
import com.syz.init.MethodToClassManager;
import com.syz.init.bean.MethodBean;

@Service("dubboBaseService")
public class DubboBaseServiceImpl implements DubboBaseService {

	@Override
	public DubboOutParamBean doPost(DubboInParamBean dubboInParamBean) {
		DubboOutParamBean dubboOutParamBean = new DubboOutParamBean();
		String txid = dubboInParamBean.getTxid();
		String inParam = dubboInParamBean.getParam();
		dubboOutParamBean.setTxid(txid);
		MethodBean methodBean = MethodToClassManager.getGlobal().getMethodBean(txid.toUpperCase());
		if (null == methodBean) {
			// 返回失败对象
			dubboOutParamBean.setParam("未找到对应的service接口！");
		} else {
			try {
				Class<?> serviceClazz = Class.forName(methodBean.getClassName());
				Class<?> paramClazz = Class.forName(methodBean.getParamClassName());
				ObjectMapper om = new ObjectMapper();
				BaseRequestMsg paramObj = (BaseRequestMsg) om.readValue(inParam, paramClazz);
				BaseResponseMsg resp = (BaseResponseMsg) invokeMethod(serviceClazz, txid, paramObj);
				String outParam = om.writeValueAsString(resp);
				dubboOutParamBean.setParam(outParam);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dubboOutParamBean;
	}

	/**
	 * 执行方法并返回结果
	 * 
	 * @Title: invokeMethod
	 * @Description: TODO
	 * @param clazz
	 * @param methodName
	 * @param paramObj
	 * @return
	 * @throws Exception
	 * @return: Object
	 */
	public <T> Object invokeMethod(Class<?> clazz, String methodName, Object paramObj) throws Exception {
		try {
			Method method = clazz.getMethod(methodName.toLowerCase(), new Class[] { paramObj.getClass() });// 方法名称、方法参数类型；多个参数类型形式如，new
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			Object obj = MyBeanFactoryPostProcessor.getBean(clazz);
			return method.invoke(obj, paramObj);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new Exception("接口" + clazz.getName() + " 没有找到方法 " + methodName + "()", e);
		}
	}

}