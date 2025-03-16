package com.image.processing.api.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(ImageException.class)
//    public ResponseEntity<ErrorDetails> imageExceptionHandler(ImageException pe , WebRequest req)
//    {
//        ErrorDetails err  = new ErrorDetails();
//        err.setDescription(req.getDescription(false));
//        err.setTimestamp(LocalDateTime.now());
//        err.setMessage(pe.getMessage());
//
//        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
//
//    }

//    @ExceptionHandler(RequestException.class)
//    public ResponseEntity<ErrorDetails> requestExceptionHandler(ImageException pe , WebRequest req)
//    {
//        ErrorDetails err  = new ErrorDetails();
//        err.setDescription(req.getDescription(false));
//        err.setTimestamp(LocalDateTime.now());
//        err.setMessage(pe.getMessage());
//
//        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
//
//    }
//
//    @ExceptionHandler(WebhookException.class)
//    public ResponseEntity<ErrorDetails> webhookExceptionHandler(ImageException pe , WebRequest req)
//    {
//        ErrorDetails err  = new ErrorDetails();
//        err.setDescription(req.getDescription(false));
//        err.setTimestamp(LocalDateTime.now());
//        err.setMessage(pe.getMessage());
//
//        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
//
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException pe)
    {
        ErrorDetails err  = new ErrorDetails();
        err.setDescription("getting Error");
        err.setTimestamp(LocalDateTime.now());
        err.setMessage(Objects.requireNonNull(pe.getFieldError()).getDefaultMessage());
        return new ResponseEntity<>(err,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> exceptionHandler(Exception e) {
        ErrorDetails err = new ErrorDetails();
        err.setDescription("exception occurs ");
        err.setTimestamp(LocalDateTime.now());
        err.setMessage(e.getMessage());

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> runtimeExceptionHandler(RuntimeException e) {
        ErrorDetails err = new ErrorDetails();
        err.setDescription("exception occurs ");
        err.setTimestamp(LocalDateTime.now());
        err.setMessage(e.getMessage());

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
}
