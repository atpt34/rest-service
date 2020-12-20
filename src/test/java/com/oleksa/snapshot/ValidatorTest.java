package com.oleksa.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.validation.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ValidatorTest {

    @AllArgsConstructor
    @Data
    static class Hello{
        @NotNull(message = "It's null!!!!") private String name;
//        @Valid private Hello hello;

        private Optional<@Max(300) Integer> maxCapacity = Optional.empty();
    }

    @Test
    void test() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Hello hello = new Hello("blah", Optional.empty());
        var violations = validator.validate(hello);
        assertThat(violations).isEmpty();
        hello.setName(null);
        violations = validator.validate(hello);
        assertThat(violations).isNotEmpty();
//        throw new ConstraintViolationException(violations);
//        Hello unwrap = validator.unwrap(Hello.class); // not supported
//        System.out.println(unwrap);
    }
}
