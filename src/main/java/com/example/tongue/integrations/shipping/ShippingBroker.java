package com.example.tongue.integrations.shipping;

import com.example.tongue.locations.models.Location;
import com.example.tongue.shopping.models.Order;

public interface ShippingBroker {

    // If something fails, a null object must be returned
    ShippingBrokerResponse requestDriver(Order order);
    ShippingBrokerResponse requestShippingSummary(Location origin,Location destination);

}
