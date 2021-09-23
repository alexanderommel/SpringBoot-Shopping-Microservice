package com.example.tongue.merchants.webservices;

import com.example.tongue.core.utilities.RestControllerRoutines;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.ProductImage;
import com.example.tongue.merchants.enumerations.ProductStatus;
import com.example.tongue.merchants.repositories.ProductImageRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController()
public class ProductsRestController {

    private ProductRepository repository;
    private ProductImageRepository imageRepository;
    private StoreVariantRepository storeVariantRepository;

    public ProductsRestController(@Autowired ProductRepository repository,
                                  @Autowired ProductImageRepository imageRepository,
                                  @Autowired StoreVariantRepository storeVariantRepository){
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.storeVariantRepository=storeVariantRepository;
    }

    /*
    GET MAPPINGS ----------------------------------------------------------------------------------------------------------- START
     */

    @GetMapping(value = "/products",params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0",required = false) int page
            , @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Product> productPage = repository.findAll(pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products/{id}")
    public ResponseEntity<Map<String,Object>> one(@PathVariable Long id){
            Product product = repository.getById(id);
            return getResponseEntityByProduct(product);
    }

    @GetMapping(value = "/products/{id}/images")
    public ResponseEntity<Map<String,Object>> getImagesByProductId(@PathVariable Long id){
        try {
            List<ProductImage> images = imageRepository.findAllByProduct_Id(id);
            if (images.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("images",images);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/products",params = {"ids"}) // format type id1,id2,id3
    public ResponseEntity<Map<String,Object>> getProductsByIds(@RequestParam String ids){
        try {
            String regex = "([0-9]{1,10},){0,15}[0-9]{1,15}";
            Boolean matches = Pattern.matches(regex,ids);
            if(matches==false){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            String[] productIds = ids.split(",");
            List<Long> longIds = Stream.of(productIds).map(Long::valueOf).collect(Collectors.toList());
            List<Product> products = repository.findByIdIn(longIds);
            if (products.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("products",products);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     /*
    GET MAPPINGS ----------------------------------------------------------------------------------------------------------- END
     */

     /*
    POST MAPPINGS ----------------------------------------------------------------------------------------------------------- START
     */

     /*
    POST MAPPINGS ----------------------------------------------------------------------------------------------------------- END
     */
    @PostMapping(value = "/products")
    public ResponseEntity<Map<String,Object>> insert(@Valid @RequestBody Product product){
        try {
            Map<String,Object> response = new HashMap<>();
            product.setId(null);
            product.setOriginalPrice(product.getPrice());
            String adjustments = String.valueOf(product.getOriginalPrice());
            product.setAdjustments(adjustments);
            response.put("product",repository.save(product));
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
     /*
    PUT MAPPINGS ----------------------------------------------------------------------------------------------------------- START
     */

    @PutMapping(value = "/products/{id}")
    public ResponseEntity<Map<String,Object>> update(@PathVariable Long id,@RequestBody Product product){
        try {
            Product original = repository.getById(id);
            Map<String,Object> response = new HashMap<>();
            if(original==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if(product.getDescription()!=null){
                original.setDescription(product.getDescription());
            }
            if (product.getHandle()!=null){
                original.setHandle(product.getHandle());
            }
            if(product.getTags()!=null){
                String regex = "([a-zA-Z0-9]{1,10},){1,15}[a-zA-Z0-9]{1,10}";
                Boolean matches = Pattern.matches(regex,product.getTags());
                if (matches){
                    original.setTags(product.getTags());
                }else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            if(product.getType()!=null){
                original.setType(product.getType());
            }
            if(product.getStatus()!=null){
                original.setStatus(product.getStatus());
            }
            if(product.getTitle()!=null){
                original.setTitle(product.getTitle());
            }

            if(product.getInventorId()!=null){
                original.setInventorId(product.getInventorId());
            }
            if(product.getPrice()!=null){
                String adjustments = original.getAdjustments();
                adjustments = adjustments + "," + String.valueOf(product.getPrice());
                original.setPrice(product.getPrice());
                original.setAdjustments(adjustments);
            }
            response.put("product",repository.save(original));
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     /*
    PUT MAPPINGS ----------------------------------------------------------------------------------------------------------- END
     */

    /*
    Filtering by product fields ------------------------------------------------------------------------------------------ START
     */

    @GetMapping(value = "/products", params = {"store_variant_id","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByStoreVariant(
            @RequestParam Long store_variant_id,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "50", required = false) int size){

        Pageable pageable = PageRequest.of(page,size);
        Page<Product> productPage = repository.findAllByStoreVariantId(store_variant_id,pageable);
        return RestControllerRoutines.getResponseEntityByPageable(productPage,"products");

    }

    @GetMapping(value = "/products", params = {"description","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByDescription(@RequestParam String description,
                                                 @RequestParam(defaultValue = "0",required = false) int page,
                                                 @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByDescription( description,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"handle","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByHandle(@RequestParam String handle,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByHandle( handle,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"title","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByTitle(@RequestParam String title,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByTitle( title,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"type","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByType(@RequestParam String type,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByType( type,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"status","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByStatus(@RequestParam ProductStatus status,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByStatus( status,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"tags","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByTags(@RequestParam String tags,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByTags( tags,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"inventor_id","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByInventorId(@RequestParam String inventor_id,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByInventorId( inventor_id,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"price","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByPrice(@RequestParam Double price,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByPrice( price,  pageable);
        return getResponseEntityByPageable(productPage);
    }

    @GetMapping(value = "/products", params = {"originalprice","page","size"})
    public ResponseEntity<Map<String,Object>> findAllByOriginalPrice(@RequestParam Double originalprice,
                                                                   @RequestParam(defaultValue = "0",required = false) int page,
                                                                   @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = repository.findAllByOriginalPrice(pageable,originalprice);
        return getResponseEntityByPageable(productPage);
    }


     /*
    Filtering by product fields ------------------------------------------------------------------------------------------- END
     */

     /*
    INTERNAL METHODS ------------------------------------------------------------------------------------------------------ START
     */

    private ResponseEntity<Map<String,Object>> getResponseEntityByProduct(Product product){
        try {
            if (product==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("product",product);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String,Object>> getResponseEntityByPageable(Page page){
        try {
            if (page==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<Product> products = page.getContent();
            Map<String,Object> response = new HashMap<>();
            response.put("products",products);
            response.put("page",page.getNumber());
            response.put("pages",page.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
