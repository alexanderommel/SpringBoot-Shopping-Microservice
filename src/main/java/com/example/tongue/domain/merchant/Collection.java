package com.example.tongue.domain.merchant;

import com.example.tongue.domain.merchant.enumerations.CollectionStatus;
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
public class Collection {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({"location","representative","plan",
    "representativePhone","postalCode","currency_code",
    "allowCashPayments","enabledCurrencies","country_code",
    "hasActiveDiscounts","name","store",
    "storeFoodType","storeImageURL"})
    private StoreVariant storeVariant;

    @NotNull
    private String title;
    private String tag;
    private String imageUrl;
    private CollectionStatus status;

}
