package com.example.tongue.integrations.shipping;

import com.example.tongue.core.domain.Position;

public interface ShippingBroker {

    ShippingBrokerResponse requestShippingSummary(Position origin, Position destination);

}
