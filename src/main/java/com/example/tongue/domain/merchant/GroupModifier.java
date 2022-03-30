package com.example.tongue.domain.merchant;

import com.example.tongue.domain.merchant.enumerations.GroupModifierType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupModifier {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    @JsonIgnoreProperties({"status","tags","inventorId","createdAt",
            "originalPrice","adjustments","currency_code",
            "price","type","title","handle","description","type"})
    private Product product;

    @ManyToOne
    @JsonIgnore
    private StoreVariant storeVariant;

    @NotNull
    private GroupModifierType type;

    @NotNull
    private String context;

    // Useful when group type modifier is mandatory
    private int maximumActiveModifiers=1;
    private int minimumActiveModifiers=1;

}
