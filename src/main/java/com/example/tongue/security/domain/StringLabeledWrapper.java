package com.example.tongue.security.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringLabeledWrapper<T>{

    private T covered;
    private String label;

}
