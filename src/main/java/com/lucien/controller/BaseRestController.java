package com.lucien.controller;

import com.lucien.response.ValidationError;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Lucien on 2017/6/9.
 */
@RestController
public class BaseRestController {

    protected ValidationError responseFieldErrors(List<FieldError> fieldErrors) {
        ValidationError dto = new ValidationError();

        for (FieldError fieldError: fieldErrors) {
            dto.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return dto;
    }
}
