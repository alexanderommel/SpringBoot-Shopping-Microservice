package com.example.tongue.domain.merchant;

import com.example.tongue.security.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

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

    @OneToOne @NotNull
    Account account;

}
