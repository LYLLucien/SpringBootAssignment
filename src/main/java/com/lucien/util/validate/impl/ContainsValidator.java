package com.lucien.util.validate.impl;

import com.lucien.util.validate.Contains;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by Lucien on 2017/5/28.
 */
public class ContainsValidator implements ConstraintValidator<Contains, Object> {
    private String[] values;

    @Override
    public void initialize(Contains annotation) {
        values = annotation.values();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (values.length <= 0) {
            return true;
        } else {
            for (String child : values) {
                if (child.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
