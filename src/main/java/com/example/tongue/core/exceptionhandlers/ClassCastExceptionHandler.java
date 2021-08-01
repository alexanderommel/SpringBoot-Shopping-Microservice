package com.example.tongue.core.exceptionhandlers;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ClassCastExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ClassCastException.class)

    public ClassCastExceptionHandler.Error ClassCastException(ClassCastException ex) {
        ClassCastExceptionHandler.Error error = new ClassCastExceptionHandler.Error(BAD_REQUEST.value(), "validation error");
        error.addFieldError("raton", ex.getMessage());
        return error;
    }

    private ClassCastExceptionHandler.Error processFieldErrors(List<FieldError> fieldErrors) {
        ClassCastExceptionHandler.Error error = new ClassCastExceptionHandler.Error(BAD_REQUEST.value(), "validation error");
        for (org.springframework.validation.FieldError fieldError : fieldErrors) {
            error.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error;
    }

    static class Error {
        private final int status;
        private final String message;
        private List<FieldError> fieldErrors = new ArrayList<>();

        Error(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public void addFieldError(String path, String message) {
            FieldError error;
            error = new FieldError("ISO 8601 Date", path, message);
            System.out.println("ASDASDASDASDASD");
            fieldErrors.add(error);
        }

        public List<FieldError> getFieldErrors() {
            return fieldErrors;
        }
    }
}
