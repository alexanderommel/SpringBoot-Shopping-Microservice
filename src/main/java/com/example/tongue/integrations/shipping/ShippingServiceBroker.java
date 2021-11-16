package com.example.tongue.integrations.shipping;

import com.example.tongue.locations.models.Location;
import com.example.tongue.shopping.models.Order;
import com.example.tongue.shopping.models.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingServiceBroker {

    //Fields
    private RestTemplate restTemplate;
    private static String shippingRequestURl=
            ShippingServiceInformer.getAuthority()+"shipping/request_driver";

    public ShippingServiceBroker(@Autowired RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    public ResponseEntity<Map<String,Object>> requestDriver(Order order){
        Map<String,Object> response = new HashMap<>();
        OrderStatus status = order.getOrderStatus();
        if (status==OrderStatus.CREATED){
            Long artifactId = order.getId();
            Shipping shipping = new Shipping();
            shipping.setArtifactId(artifactId);
            shipping.setClientDomain("TongueShoppingService");
            shipping.setCustomerAddress("Nayon 6 de Diciembre");
            Location destination = order.getDestination();
            Position destinationP = new Position();
            destinationP.setLatitude(destination.getLatitude());
            destinationP.setLongitude(destination.getLongitude());
            shipping.setDestination(destinationP);
            Shipping shipping1 = restTemplate.getForObject(
                    shippingRequestURl,
                    Shipping.class,
                    shipping
            );
            if (shipping1==null){
                response.put("error","No drivers available");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.put("shipping",shipping1);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.put("error","Order Status must be CREATED");
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

}
