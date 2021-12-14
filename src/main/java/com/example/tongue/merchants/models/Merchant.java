package com.example.tongue.merchants.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Merchant {
    @Id
    @GeneratedValue
    private Long id;
    private String ownerName;
    private String email;
    private String phoneNumber;

}
