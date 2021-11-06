# sensitive-util
数据脱敏工具包，提供注解用于配置脱敏字段。
基于SpringBoot2.1.17.RELEASE版本，使用AOP在@RestController所有方法或@ResponseBody注解的方法处理返回值。
###
1. 引入依赖（中央仓库审核中）
```
	<dependency>
		<groupId>io.github.cisumer</groupId>
  		<artifactId>sensitive-util</artifactId>
  		<version>1.1</version>
	</dependency>
```
2. 在待脱敏处理的字段上增加@SensitiveInfo注解
3. 配置脱敏类型SensitiveType，目前支持中文名（CHINESE_NAME）、EMAIIL、身份证(ID_CARD)、银行卡（BANK_CARD）、手机号（MOBILE_PHONE）、座机号（FIXED_PHONE）、密码（PASSWORD）、地址（ADDRESS）、秘钥（SECURE_KEY）、自定义（CUSTOM）
4. 深度copy对象，对clone对象进行脱敏操作
5. TODO 扩展日志记录的脱敏操作
