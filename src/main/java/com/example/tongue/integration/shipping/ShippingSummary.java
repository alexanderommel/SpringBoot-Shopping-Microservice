package com.example.tongue.integration.shipping;

import java.math.BigDecimal;

public class ShippingSummary {

    private BigDecimal fee;
    private Double deliveryTime;
    private Double distance;

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Double getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Double deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

}
