package com.example.tongue.integration.shipping;

import com.example.tongue.core.domain.Position;

public interface ShippingBroker {

    ShippingBrokerResponse requestShippingSummary(Position origin, Position destination);

}
