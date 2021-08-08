package com.example.tongue.merchants.webservices;

import com.example.tongue.merchants.repositories.GroupModifierRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupModifierRestController {

    // Fields
    private GroupModifierRepository groupModifierRepository;

    // Constructor
    public GroupModifierRestController(@Autowired GroupModifierRepository groupModifierRepository){
        this.groupModifierRepository = groupModifierRepository;
    }

    // Methods

}
