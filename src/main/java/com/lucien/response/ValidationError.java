package com.lucien.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucien on 2017/6/8.
 */
public class ValidationError {

    private List<FieldError> fieldErrors = new ArrayList<>();

    public ValidationError() {

    }

    public void addFieldError(String path, String message) {
        FieldError error = new FieldError(path, message);
        fieldErrors.add(error);
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
