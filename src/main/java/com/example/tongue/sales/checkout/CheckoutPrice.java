package com.example.tongue.sales.checkout;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class CheckoutPrice {
    private BigDecimal cartTotal;
    private BigDecimal cartSubtotal;
    private BigDecimal shippingTotal;
    private BigDecimal shippingSubtotal;
    private BigDecimal checkoutTotal;
    private BigDecimal checkoutSubtotal;

    public BigDecimal getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }

    public BigDecimal getCartSubtotal() {
        return cartSubtotal;
    }

    public void setCartSubtotal(BigDecimal cartSubtotal) {
        this.cartSubtotal = cartSubtotal;
    }

    public BigDecimal getShippingTotal() {
        return shippingTotal;
    }

    public void setShippingTotal(BigDecimal shippingTotal) {
        this.shippingTotal = shippingTotal;
    }

    public BigDecimal getShippingSubtotal() {
        return shippingSubtotal;
    }

    public void setShippingSubtotal(BigDecimal shippingSubtotal) {
        this.shippingSubtotal = shippingSubtotal;
    }

    public BigDecimal getCheckoutTotal() {
        return checkoutTotal;
    }

    public void setCheckoutTotal(BigDecimal checkoutTotal) {
        this.checkoutTotal = checkoutTotal;
    }

    public BigDecimal getCheckoutSubtotal() {
        return checkoutSubtotal;
    }

    public void setCheckoutSubtotal(BigDecimal checkoutSubtotal) {
        this.checkoutSubtotal = checkoutSubtotal;
    }
}
