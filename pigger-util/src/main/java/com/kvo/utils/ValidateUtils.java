package com.kvo.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public abstract class ValidateUtils {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 验证Bean
     *
     * @param Object
     * @param <T>
     * @return
     */
    public static <T> Set<ConstraintViolation<T>> validate(T Object) {
        return validator.validate(Object);
    }

    /**
     * 验证结果转String
     *
     * @param violations
     * @param <T>
     * @return
     */
    public static <T> String validateResultToString(Set<ConstraintViolation<T>> violations) {
        StringBuilder errMsg = new StringBuilder();
        for (ConstraintViolation<T> violation : violations) {
            errMsg.append(violation.getPropertyPath().toString() + " " + violation.getMessage() + ", ");
        }
        return errMsg.toString();
    }

}
