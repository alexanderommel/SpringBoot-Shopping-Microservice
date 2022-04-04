package com.example.tongue.domain.merchant;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreVariant {

    private @Id @GeneratedValue Long id;

    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Store store;

    @Embedded
    private Position location;

    private String representative;

    private String representativePhone;

    private String postalCode;

    private Boolean allowCashPayments=true;

    private Boolean hasActiveDiscounts=false; //Useful to avoid searching for discounts

    private String storeImageURL;

    @NotEmpty(message = "Currency code must not be empty")
    @Pattern(regexp = "(USD)") //ISO 4217
    private String currency;

    // Tongue Machine Learning server must provide the most probable
    // classification on creation time
    private StoreVariantType storeFoodType=StoreVariantType.CHICKEN;

    @JsonIgnore
    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

}
