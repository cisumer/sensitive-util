package com.github.cisumer.sensitive.web;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.github.cisumer.sensitive.annotation.SensitiveInfo;
import com.github.cisumer.sensitive.util.SensitiveConvertUtil;

@SuppressWarnings("rawtypes")
public class SensitiveInterceptor implements MethodInterceptor{
	public Object invoke(MethodInvocation i)throws Throwable{
		Method m=i.getMethod();
		Object result=i.proceed();
		Class resultType=m.getReturnType();
		if(resultType.equals(String.class)){
			SensitiveInfo ann=m.getAnnotation(SensitiveInfo.class);
			return SensitiveConvertUtil.convert((String)result, ann);
		}
		return SensitiveConvertUtil.convert(result);
	}
}
