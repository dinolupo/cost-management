package com.github.dinolupo.cm.business.boundary.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.OptimisticLockException;

public class GenericAdvice {
    @ResponseBody
    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    String optimisticLockHandler(OptimisticLockException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String optimisticLockDbHandler(OptimisticLockingFailureException ex) {
        return ex.getMessage();
    }
}
