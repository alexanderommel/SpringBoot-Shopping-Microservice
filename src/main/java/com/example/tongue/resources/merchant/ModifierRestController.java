package com.example.tongue.resources.merchant;

import com.example.tongue.repositories.merchant.ModifierRepository;
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
