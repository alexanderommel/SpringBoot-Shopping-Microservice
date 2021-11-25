package com.example.tongue.integrations.shipping;

import com.example.tongue.locations.models.Location;
import com.example.tongue.shopping.models.Order;

public interface ShippingBroker {

    // If something fails, a null object must be returned
    public Driver requestDriver(Order order);
    public ShippingSummary requestShippingSummary(Location origin,Location destination);
    public ShippingServiceError getErrors();

}
