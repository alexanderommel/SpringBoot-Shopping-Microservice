package com.example.tongue.resources.merchant;

import com.example.tongue.domain.merchant.GroupModifier;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.repositories.merchant.GroupModifierRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GroupModifierRestController {

    // Fields
    private GroupModifierRepository groupModifierRepository;
    private ModifierRepository modifierRepository;

    // Constructor
    public GroupModifierRestController(@Autowired GroupModifierRepository groupModifierRepository,
                                       @Autowired ModifierRepository modifierRepository) {
        this.groupModifierRepository = groupModifierRepository;
        this.modifierRepository = modifierRepository;
    }

    // Methods

    @GetMapping(value = "/group_modifiers", params = {"page", "size"})
    public ResponseEntity<Map<String, Object>> all(@RequestParam(defaultValue = "0", required = false) int page
            , @RequestParam(defaultValue = "50", required = false) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GroupModifier> groupModifierPage = groupModifierRepository.findAll(pageable);
        return getResponseEntityByPageable(groupModifierPage);
    }

    @GetMapping(value = "/group_modifiers", params = {"productId"})
    public ResponseEntity<Map<String,Object>> allByProductId(@RequestParam(required = true) Long productId){

        try {
            List<GroupModifier> groupModifiers = groupModifierRepository.findAllByProduct_Id(productId);
            if (groupModifiers==null || groupModifiers.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("group_modifiers",groupModifiers);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group_modifiers/{id}/modifiers")
    public ResponseEntity<Map<String,Object>> getModifiersByGroup(@PathVariable Long id){
        try {
            List<Modifier> modifiers = modifierRepository.findAllByGroupModifier_Id(id);
            if (modifiers.isEmpty() || modifiers==null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No modifiers associated with group_modifier id: "+id);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("modifiers",modifiers);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private ResponseEntity<Map<String, Object>> getResponseEntityByPageable(Page page) {
        try {
            if (page == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<GroupModifier> groupModifiers = page.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("group_modifiers", groupModifiers);
            response.put("page", page.getNumber());
            response.put("pages", page.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
