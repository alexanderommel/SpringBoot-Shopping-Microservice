package com.example.tongue.core.exceptions;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumerationContainsException<E extends Enum<E>>{

    private Set<String> enums;
    private String field;
    private String value;
    private String enumClass;

    public EnumerationContainsException(Class<E> elementType, String field, String value){

        // Fill enum constants
        enums =
                EnumSet.allOf(elementType).
                        stream().map(Enum::name).
                        collect(Collectors.toSet());

        // Fill other fields
        this.enumClass=elementType.getSimpleName();
        this.field=field;
        this.value=value;
    }

    public Set<String> getEnums() {
        return enums;
    }

    public void setEnums(Set<String> enums) {
        this.enums = enums;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
