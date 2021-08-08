package com.example.tongue.merchants.webservices;

import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModifierRestController {

    // Fields
    private ModifierRepository modifierRepository;

    // Constructor
    public ModifierRestController(@Autowired ModifierRepository modifierRepository){
        this.modifierRepository = modifierRepository;
    }

    // Methods

}
