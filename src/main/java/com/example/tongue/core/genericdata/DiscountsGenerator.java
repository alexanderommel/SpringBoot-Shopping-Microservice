package com.example.tongue.core.genericdata;

import com.example.tongue.domain.merchant.enumerations.DiscountType;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Product;

import java.util.List;

public class DiscountsGenerator {

    public static void generarDescuentos(List<Product> products){
        Discount discount = new Discount();
        discount.setUsageLimit(1);
        discount.setPriority(1);
        discount.setMaximumAmount(100.0);
        discount.setAutoApplicable(true);
        discount.setDiscountType(DiscountType.PRODUCT);
    }
}
