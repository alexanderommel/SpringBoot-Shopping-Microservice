package com.example.tongue.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Product")
public class ProductNotFoundException extends RuntimeException{
    private Long id;
    public ProductNotFoundException(){}
    public ProductNotFoundException(Long id){this.id=id;}
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
