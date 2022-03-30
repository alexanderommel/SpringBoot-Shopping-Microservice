package com.example.tongue.domain.checkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Position {

    private Float latitude;
    private Float longitude;
    private String address;
    private String owner="<span>";

    public Boolean isValid(){
        return  !(latitude==null || longitude==null);
    }

}
