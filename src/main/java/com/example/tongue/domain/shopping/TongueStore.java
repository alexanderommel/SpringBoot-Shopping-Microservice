package com.example.tongue.domain.shopping;


import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.integration.shipping.ShippingSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TongueStore implements Serializable {

    private StoreVariant storeVariant;
    private ShippingSummary shippingSummary;

}
