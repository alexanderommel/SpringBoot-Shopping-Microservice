package com.example.tongue.core.contracts;

import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;

import java.math.BigDecimal;


public class ASD {

    ApiResponse response = ApiResponse.error("Hola");
    ApiResponse getResponse = ApiResponse.success("Alexander");

    public void asd(){


        // HAMBURGUESAS

        Product h1 = Product.builder()
                .title("aqui el nombre del producto")
                .description("aqui la descripcion")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(3.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(null)
                .collection(null)
                .imageUrl("aqui pon la url de la imagen")
                .build();

        Product h2 = Product.builder()
                .title("aqui el nombre del producto")
                .description("aqui la descripcion")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(3.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(null)
                .collection(null)
                .imageUrl("aqui pon la url de la imagen")
                .build();

        // COMIDA COSTEÑA

        Product c1 = Product.builder()
                .title("aqui el nombre del producto")
                .description("aqui la descripcion")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(3.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(null)
                .collection(null)
                .imageUrl("aqui pon la url de la imagen")
                .build();


        // PIZZAS




    }

}
