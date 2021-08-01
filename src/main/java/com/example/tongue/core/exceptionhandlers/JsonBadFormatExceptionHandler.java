package com.example.tongue.core.exceptionhandlers;

import com.example.tongue.core.exceptions.JsonBadFormatException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class JsonBadFormatExceptionHandler {
    @ResponseStatus(value = BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(JsonBadFormatException.class)
    public Error JsonBadFormatException(JsonBadFormatException ex) {
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setReason("Request body is not JSON valid");
        return error;
    }
    static class Error{
        private HttpStatus error;
        private String reason;

        public HttpStatus getError() {
            return error;
        }

        public void setError(HttpStatus error) {
            this.error = error;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
