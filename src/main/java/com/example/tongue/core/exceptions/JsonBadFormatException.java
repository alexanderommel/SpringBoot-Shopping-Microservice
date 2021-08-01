package com.example.tongue.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Request must be a JSON String")
public class JsonBadFormatException extends RuntimeException{
}
