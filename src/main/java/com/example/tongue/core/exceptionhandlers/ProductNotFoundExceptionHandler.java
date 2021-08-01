package com.example.tongue.core.exceptionhandlers;

import com.example.tongue.core.exceptions.JsonBadFormatException;
import com.example.tongue.core.exceptions.ProductNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ProductNotFoundExceptionHandler {
    @ResponseStatus(value = NOT_FOUND)
    @ResponseBody
    //@ExceptionHandler(ProductNotFoundException.class)
    public Error ProductNotFoundException(ProductNotFoundException ex) {
        Error error = new Error();
        error.setError(NOT_FOUND);
        Long id = ex.getId();
        error.setReason("No such Product");
        if (id!=null) error.setId(id);
        return error;
    }
    static class Error{
        private HttpStatus error;
        private String reason;
        private Long id;


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

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
