package com.example.tongue.integrations.shipping;

import com.example.tongue.locations.models.Location;
import com.example.tongue.shopping.models.Order;
import com.example.tongue.shopping.models.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


public class ShippingServiceBroker implements ShippingBroker{

    //Fields
    @Autowired
    private RestTemplate restTemplate;
    private ShippingServiceError error;


    @Override
    public Driver requestDriver(Order order) {
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
                return driver;
            }catch (RestClientResponseException e){
                ShippingServiceError serviceError =  new ShippingServiceError();
                serviceError.setCode(e.getRawStatusCode());
                error = serviceError;
            }
        }else {
            ShippingServiceError serviceError =
                    new ShippingServiceError();
            serviceError.setMessage("OrderStatus must be CREATED");
            this.error=serviceError;
        }
        return null;
    }

    @Override
    public ShippingSummary requestShippingSummary(Location origin, Location destination) {
        try{
            ShippingSummary summary = restTemplate.getForObject(
                    ShippingServiceInformer.shippingSummaryUrl,
                    ShippingSummary.class
            );
            return summary;
        }catch (RestClientResponseException e){
            ShippingServiceError serviceError =  new ShippingServiceError();
            serviceError.setCode(e.getRawStatusCode());
            error = serviceError;
        }
        return null;
    }

    @Override
    public ShippingServiceError getErrors() {
        return this.error;
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
