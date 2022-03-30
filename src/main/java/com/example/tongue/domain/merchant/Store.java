package com.example.tongue.domain.merchant;

import com.example.tongue.domain.merchant.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Store {
    private @Id @GeneratedValue Long id;
    private String owner;
    private Instant createdAt;
    private String domain;
    private String name;
    @ManyToOne
    private Merchant merchant;
    private String contactPhone;
    private String contactEmail;

}
