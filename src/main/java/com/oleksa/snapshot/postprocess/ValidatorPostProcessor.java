package com.oleksa.snapshot.postprocess;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.validation.Validator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class ValidatorPostProcessor implements BeanPostProcessor {

    /**
     * Very fast! But requires typing!
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
//    @Override
    public Object postProcessAfterInitialization3(Object bean, String beanName) throws BeansException {
        Object newBean = bean;
        if ("defaultValidator".equals(beanName)) {
            newBean = new ValidatorProxy((Validator) bean);
        }
        return newBean;
    }

    /**
     * Dynamic proxy is better since no need to write composite classes yourself!
     * Do not forget to call method on actual instance and not proxy because it will lead to stack overflow
     * Also fast after first call!
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
//    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object newBean = bean;
        if ("defaultValidator".equals(beanName)) {
            newBean = Proxy.newProxyInstance(bean.getClass().getClassLoader(),
                    new Class[]{Validator.class},
                    (proxy, method, args) -> {
                        System.out.println("inside jdk dynamic proxy! " + method.getName());
                        if ("validate".equals(method.getName())) {
                            System.out.println("start validating " + args[0].getClass());
                            StopWatch watch = new StopWatch();
                            try {
                                watch.start();
                                return method.invoke(bean, args);
                            } finally {
                                watch.stop();
                                System.out.println("validation took " + watch.getLastTaskTimeMillis() + " ms");
                            }
                        } else {
                            return method.invoke(bean, args);
                        }
                    });
        }
        return newBean;
    }

    /**
     * Cglib maybe performance better!
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
//    @Override
    public Object postProcessAfterInitialization2(Object bean, String beanName) throws BeansException {
        Object newBean = bean;
        if ("defaultValidator".equals(beanName)) {
            Callback callback = new MethodInterceptor() {
                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                    if ("validate".equals(method.getName())) {
                        System.out.println("validating with cglib: " + args[0].getClass());
                        StopWatch watch = new StopWatch();
                        try {
                            watch.start();
                            return methodProxy.invoke(bean, args);
                        } finally {
                            watch.stop();
                            System.out.println("validating intercept took: " + watch.getLastTaskTimeMillis());
                        }
                    }
                    return methodProxy.invoke(bean, args);
                }
            };
            newBean = Enhancer.create(Validator.class, callback);
        }
        return newBean;
    }
}
