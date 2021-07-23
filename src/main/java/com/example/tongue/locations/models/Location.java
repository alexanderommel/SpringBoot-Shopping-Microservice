package com.example.tongue.locations.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class Location {
    private @Id @GeneratedValue Long id;
    private String googlePlaceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean validate(){
        return true;
    }
}
