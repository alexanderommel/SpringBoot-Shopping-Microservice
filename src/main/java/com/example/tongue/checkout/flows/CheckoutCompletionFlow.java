package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.FlowMessage;
import com.example.tongue.checkout.models.ValidationResponse;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import com.example.tongue.shopping.models.Order;
import com.example.tongue.shopping.models.OrderStatus;
import com.example.tongue.shopping.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.Instant;

@Component
public class CheckoutCompletionFlow {

    @Autowired
    private CheckoutValidation checkoutValidation;
    @Autowired
    private CheckoutSession checkoutSession;
    @Autowired
    private CheckoutRepository checkoutRepository;
    @Autowired
    private OrderRepository orderRepository;


    public FlowMessage run(HttpSession httpSession){
        FlowMessage response = new FlowMessage();
        response.setSolved(false);
        Checkout checkout = checkoutSession.get(httpSession);
        if (checkout==null){
            response.setErrorMessage("You must create a Checkout first");
            return response;
        }
        ValidationResponse validationResponse =checkoutValidation.hardValidation(checkout);
        if (!validationResponse.isSolved()){
            response.setErrorMessage(validationResponse.getErrorMessage());
            return response;
        }
        checkout = persistCheckout(checkout,httpSession);
        checkoutSession.delete(httpSession);
        response.setAttribute(checkout,"checkout");
        Order order = createOrderFromCheckout(checkout);
        response.setAttribute(order,"order");
        response.setSolved(true);
        return response;
    }

    private Checkout persistCheckout(Checkout checkout,HttpSession httpSession){
        checkout.setFinishedAt(Instant.now());
        // Add principal to checkout
        Checkout checkout1 = checkoutRepository.save(checkout);
        checkoutSession.save(checkout,httpSession);
        return checkout1;
    }

    private Order createOrderFromCheckout(Checkout checkout){
        Order order = new Order();
        order.setOrderStatus(OrderStatus.CREATED);
        order.setCart(checkout.getCart());
        order.setCreatedAt(checkout.getCreated_at());
        order.setCurrency_code(checkout.getCurrency_code());
        order.setCustomer(checkout.getCustomer());
        order.setDestination(checkout.getDestination());
        order.setOrigin(checkout.getOrigin());
        order.setTotalPrice(checkout.getPrice().getCheckoutTotal());
        order.setSubtotalPrice(checkout.getPrice().getCheckoutSubtotal());
        order.setStoreVariant(checkout.getStoreVariant());
        order = orderRepository.save(order);
        return order;
    }

}
