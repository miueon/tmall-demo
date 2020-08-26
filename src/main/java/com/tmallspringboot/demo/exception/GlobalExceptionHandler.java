package com.tmallspringboot.demo.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public String defaultHandler(HttpServletRequest req, Exception e) throws Exception {
        e.printStackTrace();
        Class constraintViolationException =
                Class.forName("org.hibernate.exception.ConstraintViolationException");
        if (e.getCause() != null && constraintViolationException == e.getCause().getClass()) {
            return "violation detected, might be foreign key constraint";
        }
        return e.getMessage();
    }
}
