package com.example.tongue.domain.checkout;

import com.example.tongue.core.domain.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingInfo {

    @AttributeOverrides({
            @AttributeOverride(name="longitude",column = @Column(name="origin_longitude")),
            @AttributeOverride(name="latitude",column=@Column(name="origin_latitude")),
            @AttributeOverride(name = "address",column = @Column(name = "origin_address"))
    })
    private Position customerPosition;

    @AttributeOverrides({
            @AttributeOverride(name="longitude",column = @Column(name="destination_longitude")),
            @AttributeOverride(name="latitude",column=@Column(name="destination_latitude")),
            @AttributeOverride(name = "address",column = @Column(name = "destination_address"))
    })
    private Position storePosition;

    private String shippingSession;

    private BigDecimal fee;

}
