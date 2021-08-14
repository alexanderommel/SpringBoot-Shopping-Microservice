package com.example.tongue.sales.checkout;

public class CheckoutDiscountsLoadingFilter implements CheckoutFilter{
    @Override
    public Checkout doFilter(Checkout checkout) {
        return checkout;
    }
}
