package com.acamica.social.rest;

import javax.validation.ConstraintViolationException;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.ResponseEntity.badRequest;

@ControllerAdvice
@Order(HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        return badRequest().body(ErrorsBody.builder().toResource(bindingResult));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handle(ConstraintViolationException exception) {
        return badRequest().body(ErrorsBody.builder().toResource(exception.getConstraintViolations()));
    }
}
