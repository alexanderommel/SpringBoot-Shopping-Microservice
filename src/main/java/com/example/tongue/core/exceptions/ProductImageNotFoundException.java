package com.example.tongue.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Product Image")
public class ProductImageNotFoundException extends RuntimeException{
}
