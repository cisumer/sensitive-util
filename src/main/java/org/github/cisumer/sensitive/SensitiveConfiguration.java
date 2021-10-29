package org.github.cisumer.sensitive;

import org.aopalliance.intercept.Joinpoint;
import org.github.cisumer.sensitive.web.SensitiveInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
/**
 * 数据脱敏，使用aop切入controller，处理返回值
 * @author github.com/cisumer
 *
 */
@Configuration
@ConditionalOnClass(Joinpoint.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class SensitiveConfiguration{
	@Bean
	public SensitiveInterceptor sensitiveInterceptor(){
		return new SensitiveInterceptor();
	}
	
	@Bean
	public DefaultPointcutAdvisor sensitivePointcutAdvisor(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("within(@org.springframework.web.bind.annotation.RestController *) || @annotation(org.springframework.web.bind.annotation.ResponseBody)");
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(sensitiveInterceptor());
        advisor.setOrder(Ordered.LOWEST_PRECEDENCE);//降低优先级，即其他切面执行完再走这个
        return advisor;
	}
	
}
