package com.github.cisumer.sensitive.annotation;

/**
 * 定义脱敏类型即规则，支持自定义
 * @author github.com/cisumer
 *
 */
public enum SensitiveType {
	/**
	   * 自定义
	   */
	  CUSTOM,
	/**
	   * 中文名,只显示最后一个字
	   */
	  CHINESE_NAME,
	/**
	   * 中文名，显示姓和最后一个字，如果是两个字则只显示姓
	   */
	  CHINESE_NAME_MID,
	/**
	   * 中文名，只显示姓
	   */
	  CHINESE_NAME_FIRST,
	/**
	   * 密码,全隐藏
	   */
	  PASSWORD,
	  /**
	   * 身份证号，显示前六位, 后四位
	   */
	  ID_CARD,
	  /**
	   * 座机号，显示后四位
	   */
	  FIXED_PHONE,
	  /**
	   * 手机号，显示前三位和后四位
	   */
	  MOBILE_PHONE,
	  /**
	   * 地址，只显示前六个字
	   */
	  ADDRESS,
	  /**
	   * 电子邮件，显示@前第一个字符和@后的字符
	   */
	  EMAIL,
	  /**
	   * 银行卡，显示前六位，后四位
	   */
	  BANK_CARD,
	  /**
	   * 秘钥，显示后三位
	   */
	  SECURE_KEY,
	  /**
	   * 默认值，原始输出
	   */
	  DEFAULT,
}
