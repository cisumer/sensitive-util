package io.github.cisumer.sensitive;

import org.aopalliance.intercept.Joinpoint;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;

import io.github.cisumer.sensitive.web.SensitiveInterceptor;

@Configuration
@ConditionalOnProperty(name="sensitive.interceptor.enabled",havingValue="true")
@ConditionalOnClass(Joinpoint.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class SensitiveInterceptorConfiguration {
	/**
	 * 数据脱敏自动配置类，使用aop切入controller，处理返回值<br/>
	 * sensitive.interceptor.web.enabled=true时开启
	 * @author github.com/cisumer
	 *
	 */
	@Configuration
	@ConditionalOnProperty(name="sensitive.interceptor.web.enabled",havingValue="true",matchIfMissing=true)
	class SensitiveControllerInterceptorConfiguration{
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
	/**
	 * 数据脱敏，使用aop切入Log，处理参数<br/>
	 * sensitive.interceptor.web.enabled=true时开启
	 * @author github.com/cisumer
	 *
	 */
	@Configuration
	@ConditionalOnProperty(name="sensitive.interceptor.log.enabled",havingValue="true",matchIfMissing=true)
	public class SensitiveLogInterceptorConfiguration{
//		@Bean
		public DefaultPointcutAdvisor sensitivePointcutAdvisor(){
	        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
//	        pointcut.setExpression("");
	        // 配置增强类advisor
	        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
	        advisor.setPointcut(pointcut);
//	        advisor.setAdvice(sensitiveInterceptor());
	        advisor.setOrder(Ordered.LOWEST_PRECEDENCE);//降低优先级，即其他切面执行完再走这个
	        return advisor;
		}
		
	}
}
