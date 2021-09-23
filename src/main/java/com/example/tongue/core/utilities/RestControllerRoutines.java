package com.example.tongue.core.utilities;

import com.example.tongue.merchants.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestControllerRoutines {
    public static ResponseEntity<Map<String,Object>> getResponseEntityByPageable(
            Page page,
            String name){
        try {
            if (page==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<Product> products = page.getContent();
            Map<String,Object> response = new HashMap<>();
            response.put(name,products);
            response.put("page",page.getNumber());
            response.put("pages",page.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
