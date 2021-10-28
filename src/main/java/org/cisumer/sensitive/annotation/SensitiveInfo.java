package org.cisumer.sensitive.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cisumer.sensitive.SensitiveType;
/**
 * 用于待脱敏的字段，配置字段脱敏类型或使用自定义脱敏规则
 * @author github.com/cisumer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface SensitiveInfo {
	/**
	 * 脱敏类型
	 * @return SensitiveType
	 */
	SensitiveType value() default SensitiveType.DEFAULT;
	/**
	 * 从字符串左侧第几位开始脱敏
	 */
	int prefixLen() default 0;
	/**
	 * 到字符串右侧第几位结束脱敏
	 */
	int suffixLen() default 0;
	/**
	 * 替换字符
	 */
	String mask() default "*";
}
