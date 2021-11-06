package io.github.cisumer.sensitive.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.util.PropertySource.Comparator;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import io.github.cisumer.sensitive.annotation.SensitiveInfo;

/**
 * 数据脱敏工具类，实现简单的脱敏规则匹配
 * @author github.com/cisumer
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public interface SensitiveConvertUtil {

    /**
     * 脱敏规则
     * @param origin 原始字符串
     * @param prefixNoMaskLen 左侧需要保留几位明文字段
     * @param suffixNoMaskLen 右侧需要保留几位明文字段
     * @param maskStr 用于遮罩的字符串, 如'*'
     * @Return 脱敏后结果
     */
    public static String desValue(String origin, int prefixNoMaskLen, int suffixNoMaskLen, String maskStr) {
        if (StringUtils.isEmpty(origin)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = origin.length(); i < n; i++) {
            if (i < prefixNoMaskLen) {
                sb.append(origin.charAt(i));
                continue;
            }
            if (i > (n - suffixNoMaskLen - 1)) {
                sb.append(origin.charAt(i));
                continue;
            }
            sb.append(maskStr);
        }
        return sb.toString();
    }
    /**
     * 只显示最后一个汉字，其他隐藏为星号，比如：**伦
     */
    public static String chineseName(String fullName) {
        return desValue(fullName, 0, 1, "*");
    }
    /**
     * 显示姓和最后一个字，其他隐藏为星号，比如：张*伦
     */
    public static String chineseNameMid(String fullName) {
    	if(fullName.length()>2)
    		return desValue(fullName, 1, 1, "*");
    	else
    		return desValue(fullName, 1, 0, "*");
    }
    /**
     * 显示姓和最后一个字，其他隐藏为星号，比如：张**
     */
    public static String chineseNameFirst(String fullName) {
    	return desValue(fullName, 1, 0, "*");
    }


    /**
     * 显示前六位, 后四位，其他隐藏。比如：140101*******1234
     */
    public static String idCardNum(String id) {
        return desValue(id, 6, 4, "*");
    }

    /**
     * 座机号码，显示后四位，其他隐藏，比如 ****1234
     */
    public static String fixedPhone(String num) {
        return desValue(num, 0, 4, "*");
    }

    /**
     * 手机号码，显示前三位、后四位，其他隐藏，比如186****0590
     */
    public static String mobilePhone(String num) {
        return desValue(num, 3, 4, "*");
    }

    /**
     * 地址，只显示到地区，不显示详细地址，比如：太原市小店区****
     */
    public static String address(String address) {
        return desValue(address, 6, 0, "*");
    }

    /**
     * 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：p**@163.com
     */
    public static String email(String email) {
        int index = email.indexOf('@');
        if (index <= 1) {
            return email;
        }
        return desValue(email, 1, email.length()-index, "*");
    }

    /**
     * 前六位，后四位，其他用星号隐藏每位1个星号，比如：622260**********1234
     */
    public static String bankCard(String cardNum) {
        return desValue(cardNum, 6, 4, "*");
    }

    /**
     * 前两位后一位，比如：晋A****5
     */
    public static String carNumber(String carNumber) {
        return desValue(carNumber, 2, 1, "*");
    }

    /**
     * 全部字符都用*代替，比如：******
     */
    public static String all(String password) {
    	return desValue(password, 0, 0, "*");
    }

    /**
     * 密钥除了最后三位，全部都用*代替，比如：***xdS 脱敏后长度为6，如果明文长度不足三位，则按实际长度显示，剩余位置补*
     */
    public static String key(String key) {
        return desValue(key, 0, 3, "*");
    }
    static final ThreadLocal<Set<Object>> sources=ThreadLocal.withInitial(HashSet<Object>::new);
    static final ThreadLocal<Map<Object,Object>> targets=ThreadLocal.withInitial(ConcurrentHashMap<Object,Object>::new);
    
    /**
     * 深度拷贝并转换对象，如果对象属性带有注解这对字段脱敏处理
     * @param source
     * @return
     * @throws Exception
     */
    public static Object convert(Object source){
    	Object target=null;
		target = clone(source,null);
		sources.remove();
		targets.remove();
    	return target;
    }

    /**
     * 避免循环引用，使用ThreadLocal把当前处理的对象缓存，并判断是否已处理过相同对象
     * @param o
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
	static Object clone(Object o,SensitiveInfo ann){
		if(o==null)
			return null;  
		Class cl=o.getClass();
		if(sources.get().contains(o)){
			return targets.get().get(o);
		}
		Object result=null;
		if(BeanUtils.isSimpleValueType(cl)){//基本类型直接返回原值
	        return o;
	    }
		if(cl.isAssignableFrom(Collection.class)){//处理集合
			if(cl.isAssignableFrom(TreeSet.class))
				try {
					result= (Collection)BeanUtils.instantiateClass(cl.getDeclaredConstructor(Comparator.class),((TreeSet)o).comparator());
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			else
			   result= (Collection) BeanUtils.instantiateClass(cl);
			final Collection r=(Collection) result;
			((Collection)o).stream()
			.map(sub->clone(sub,ann))
			.map(sub->{
				if(ann!=null && String.class==cl.getComponentType()){
					return convert((String)sub, ann);
				}
				return sub;
			}).forEach(sub->{
				r.add(sub);
			});
			result=r;
		}
		if(cl.isArray()){//处理数组
			result=Array.newInstance(cl.getComponentType(),Array.getLength(o));
			for(int i=0;i<Array.getLength(result);i++){
				Object subObject=Array.get(o, i);
				Object subResult=clone(subObject,ann);
				if(ann!=null && String.class==cl.getComponentType()){
					subResult=convert((String)subResult, ann);
				}
				Array.set(result, i, subResult);
			}
		}
		if(cl.isAssignableFrom(Map.class)){//处理map,map的key不脱敏，value脱敏处理
			if(cl.isAssignableFrom(TreeMap.class))
				try {
					result= (Map)BeanUtils.instantiateClass(cl.getDeclaredConstructor(Comparator.class),((TreeMap)o).comparator());
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			else
			   result= (Map) BeanUtils.instantiateClass(cl);
			final Map m=(Map) result;
			((Map)o).keySet().stream()
			.forEach(key->{
				m.put(key,clone(((Map)o).get(key),ann));
			});
			result=m;
		}
		if(result==null){
			result= BeanUtils.instantiateClass(cl);
			try {
				copy(o,result);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		sources.get().add(o);
		targets.get().put(o, result);
		return result;		
	}

	/**
	 * 对象深拷贝，并处理脱敏字段
	 * @param source
	 * @param target
	 */
	static void copy(Object source,Object target){
		List<Field> field=new ArrayList<Field>();
		Class tempClass=source.getClass();
		Class targetClass=source.getClass();
		if(tempClass!=targetClass)throw new IllegalArgumentException("拷贝对象类型不同！");
		while(tempClass!=Object.class){
			field.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass=tempClass.getSuperclass();
		}
		final Class sourceClass=tempClass;
		field.stream()
			.peek(f->f.setAccessible(true))
			.filter(f->f.getType().isAssignableFrom(Collection.class) || f.getType().isArray() || f.getType().isAssignableFrom(Map.class))
			.forEach(f->{
				SensitiveInfo ann=f.getAnnotation(SensitiveInfo.class);			
				try {
					Field targetField = targetClass.getDeclaredField(f.getName());
					targetField.set(target, clone(f,ann));
				} catch (Exception e) {
					throw new RuntimeException(sourceClass.getName()+"字段"+f.getName()+"脱敏失败！");
				}
			});
		field.stream()
			.peek(f->f.setAccessible(true))
			.forEach(f->{
				try {
					Field targetField =targetClass.getDeclaredField(f.getName());
					SensitiveInfo ann=f.getAnnotation(SensitiveInfo.class);
					Object result=clone(f.get(source),ann);//克隆字段值
					if(ann!=null && f.getType().equals(String.class)){
						//字符串字段脱敏
						result=convert((String)result, ann);
					}//设置到目标对象
					targetField.set(target, result);
				} catch (Exception e) {
					throw new RuntimeException(sourceClass.getName()+"字段"+f.getName()+"脱敏失败！");
				}
			});
	}
	
    public static String convert(String value,SensitiveInfo info){
    	switch (info.value()) {
    	case CUSTOM:
    		return desValue(value, info.prefixLen(), info.suffixLen(), info.mask());
		case CHINESE_NAME:
			return chineseName(value);
		case CHINESE_NAME_MID:
			return chineseNameMid(value);
		case CHINESE_NAME_FIRST:
			return chineseNameFirst(value);
		case ADDRESS:
			return address(value);
		case EMAIL:
			return email(value);
		case BANK_CARD:
			return bankCard(value);
		case MOBILE_PHONE:
			return mobilePhone(value);
		case FIXED_PHONE:
			return fixedPhone(value);
		case ID_CARD:
			return idCardNum(value);
		case PASSWORD:
			return all(value);
		case SECURE_KEY:
			return key(value);
		default:
			return value;//如果没配置则默认全部脱敏
		}
    }
}
