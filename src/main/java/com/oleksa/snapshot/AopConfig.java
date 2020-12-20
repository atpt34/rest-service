package com.oleksa.snapshot;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@Aspect
public class AopConfig {

//    @Pointcut("execution(public com.oleksa.snapshot.entity.Room com.oleksa.snapshot.RoomController.getRoom(..))")
    @Pointcut("execution(public * *.getRoom(..))")
    public void monitor() { }

    @Bean
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor(true);
    }

    @Bean
    public Advisor performanceMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("com.oleksa.snapshot.AopConfig.monitor()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor());
    }
}
