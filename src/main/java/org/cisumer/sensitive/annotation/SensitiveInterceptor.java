package org.cisumer.sensitive.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.cisumer.sensitive.util.SensitiveUtil;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"rawtypes","unchecked"})
public class SensitiveInterceptor implements MethodInterceptor{
	public Object invoke(MethodInvocation i)throws Throwable{
		Method m=i.getMethod();
		Object result=i.proceed();
		Class resultType=m.getReturnType();
		if(resultType.equals(String.class)){
			SensitiveInfo ann=m.getAnnotation(SensitiveInfo.class);
			return SensitiveUtil.convert((String)result, ann);
		}
		eachField(result);
		return result;
	}
	private void eachField(Object result){
		List<Field> field=new ArrayList<Field>();
		Class c=result.getClass();
		while(c!=Object.class){
			field.addAll(Arrays.asList(c.getDeclaredFields()));
			c=c.getSuperclass();
		}
		field.stream().filter(f->f.getType().isAssignableFrom(Collection.class))
			.peek(f->f.setAccessible(true))
			.map(f->{
				try {
					return (Collection)f.get(result);
				} catch (Exception e) {
					return null;
				}
			}).filter(f->f!=null)
			.forEach(r->{
				r.stream().forEach(fi->{
					try {
						eachField(fi);
					} catch (Exception e) {e.printStackTrace();
					}
				});
			});
		field.stream()
			.peek(f->f.setAccessible(true))
			.filter(f->f.getAnnotation(SensitiveInfo.class)!=null && f.getType().equals(String.class))
			.forEach(f->setSensitiveValue(f,result));
	}
	private void setSensitiveValue(Field f,Object result){
		SensitiveInfo ann=f.getAnnotation(SensitiveInfo.class);
		try {
			String sensitiveValue=SensitiveUtil.convert((String)f.get(result), ann);
			f.set(result, sensitiveValue);
		} catch (Exception e) {
			LoggerFactory.getLogger("org.cisumer.sensitive").warn("{}字段{}脱敏失败！",result.getClass(),f.getName());
		}
	}
	
}
