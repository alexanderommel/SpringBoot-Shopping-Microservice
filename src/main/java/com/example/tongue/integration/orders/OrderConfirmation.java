package com.example.tongue.integration.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderConfirmation {

    private String orderId;
    private String customerName;
    private String courierName;
}
