package com.example.tongue.domain.merchant;

import com.example.tongue.domain.merchant.GroupModifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Modifier {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private GroupModifier groupModifier;

    private BigDecimal price= BigDecimal.valueOf(0.0);

}
