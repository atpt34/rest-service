package com.oleksa.snapshot.postprocess;

import org.springframework.util.StopWatch;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

public class ValidatorProxy implements Validator {

    private Validator target;

    public ValidatorProxy(Validator validator) {
        this.target = validator;
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
        System.out.println("manual proxy impl validating " + t.getClass());
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            return target.validate(t, classes);
        } finally {
            watch.stop();
            System.out.println("validation took " + watch.getLastTaskTimeMillis() + " ms");
        }
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes) {
        return target.validateProperty(t, s, classes);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> aClass, String s, Object o, Class<?>... classes) {
        return target.validateValue(aClass, s, o, classes);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> aClass) {
        return target.getConstraintsForClass(aClass);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return target.unwrap(aClass);
    }

    @Override
    public ExecutableValidator forExecutables() {
        return target.forExecutables();
    }
}
