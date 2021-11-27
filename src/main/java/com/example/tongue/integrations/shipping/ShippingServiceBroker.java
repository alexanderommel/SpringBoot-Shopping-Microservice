package com.example.tongue.integrations.shipping;

import com.example.tongue.locations.models.Location;
import com.example.tongue.shopping.models.Order;
import com.example.tongue.shopping.models.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ShippingServiceBroker implements ShippingBroker {

    //Fields
    private RestTemplate restTemplate;
    public ShippingServiceBroker(@Autowired RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    @Override
    public ShippingBrokerResponse requestDriver(Order order) {
        ShippingBrokerResponse response = new ShippingBrokerResponse();
        response.setSolved(false);
        OrderStatus orderStatus = order.getOrderStatus();
        if (orderStatus==OrderStatus.CREATED){
            Shipping shippingRequest =
                    createShippingRequestFromOrder(order);
            try {
                Driver driver = restTemplate.getForObject(
                        ShippingServiceInformer.shippingRequestUrl,
                        Driver.class,
                        shippingRequest
                );
                response.addMessage("driver",driver);
            }catch (RestClientResponseException e){
                response.setErrorMessage(e.getMessage());
                response.setStatusCode(e.getRawStatusCode());
            }
        }else {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setErrorMessage("OrderStatus must be CREATED");
        }
        response.setSolved(true);
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    public ShippingBrokerResponse requestShippingSummary(Location origin, Location destination) {
        ShippingBrokerResponse response = new ShippingBrokerResponse();
        response.setSolved(false);
        try{
            ShippingSummary summary = restTemplate.getForObject(
                    ShippingServiceInformer.shippingSummaryUrl,
                    ShippingSummary.class
            );
            response.addMessage("summary",summary);
        }catch (RestClientResponseException e){
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(e.getRawStatusCode());
        }
        response.setSolved(true);
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    private Shipping createShippingRequestFromOrder(Order order){
        Shipping shipping = new Shipping();
        shipping.setArtifactId(order.getId());
        shipping.setClientDomain("Shopping-Service");
        shipping.setCustomerAddress("Call Google Maps to map an addres");
        Position origin = new Position();
        Position destination = new Position();
            origin.setLongitude(order.getOrigin().getLongitude());
            origin.setLatitude(origin.getLatitude());
            destination.setLatitude(origin.getLatitude());
            destination.setLongitude(order.getOrigin().getLongitude());
        shipping.setOrigin(origin);
        shipping.setDestination(destination);
        shipping.setCustomerName(order.getCustomer().getName());
        shipping.setSender(order.getStoreVariant().getName());
        // Convert the details of the order to a readable String messa
        shipping.setMessage("Order details");
        shipping.setTotalAmount(order.getTotalPrice());
        return shipping;
    }

}
