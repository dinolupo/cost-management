package com.github.dinolupo.cm.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@ResponseBody
public class GenericAdvice {

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(PRECONDITION_FAILED)
    public ErrorInfo optimisticLockHandler(OptimisticLockException ex, HttpServletRequest req) {
         return getErrorInfo(req, ex, PRECONDITION_FAILED);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(CONFLICT)
    ErrorInfo optimisticLockDbHandler(OptimisticLockingFailureException ex, HttpServletRequest req) {
        return getErrorInfo(req, ex, CONFLICT);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    ErrorInfo elementNotFoundHandler(ElementNotFoundException ex, HttpServletRequest req) {
        return getErrorInfo(req, ex, NOT_FOUND);
    }

    private ErrorInfo getErrorInfo(HttpServletRequest req, Exception ex, HttpStatus status) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        var timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new ErrorInfo(timestamp,
                status.value(),
                status.getReasonPhrase(),
                sw.toString(),
                ex.getMessage(),
                req.getRequestURI()
        );
    }


//    // default exception handler
//    @ExceptionHandler(value = Exception.class)
//    public ModelAndView
//    defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
//        // If the exception is annotated with @ResponseStatus rethrow it and let
//        // the framework handle it - like the OrderNotFoundException example
//        // at the start of this post.
//        // AnnotationUtils is a Spring Framework utility class.
//        if (AnnotationUtils.findAnnotation
//                (e.getClass(), ResponseStatus.class) != null)
//            throw e;
//
//        // Otherwise setup and send the user to a default error-view.
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("exception", e);
//        mav.addObject("url", req.getRequestURL());
//        mav.addObject("timestamp", new Date().toString());
//        mav.setViewName(null);
//        return mav;
//    }

}
