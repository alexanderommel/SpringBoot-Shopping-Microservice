package com.example.tongue.merchants.webservices;

import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.ProductImage;
import com.example.tongue.merchants.models.ProductImageDTO;
import com.example.tongue.merchants.repositories.ProductImageRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController()
public class ProductImagesRestController {
    private ProductImageRepository repository;
    private ProductRepository productRepository;

    public ProductImagesRestController(@Autowired ProductImageRepository repository,
                                       @Autowired ProductRepository productRepository){
        this.repository=repository;
        this.productRepository=productRepository;
    }

    @GetMapping("/productimages")
    public ResponseEntity<Map<String,Object>> all(){
        try {

            List<ProductImage> imageList = repository.findAll();
            if (imageList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("images",imageList);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/productimages/{id}")
    public ResponseEntity<Map<String,Object>> one(@PathVariable Long id){
        try {
            Map<String,Object> response = new HashMap<>();
            ProductImage image = repository.getById(id);
            if(image==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            response.put("productimage",image);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/productimages", params = {"productId","heightLeq","widthLeq"})
    public ResponseEntity<Map<String,Object>> getImageByProductIdAndHeightAndWidthLeq(
            @RequestParam Long productId, @RequestParam int heightLeq, @RequestParam int widthLeq
    ){
        try {
            Map<String,Object> response = new HashMap<>();
            List<ProductImage> images =
                    repository.findAllByProduct_IdAndHeightIsLessThanEqualAndWidthIsLessThanEqual(
                            productId,heightLeq,widthLeq
                    );
            if(images.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            response.put("images",images);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/productimages",params = {"productId","squareShapedOnly"})
    public ResponseEntity<Map<String,Object>> getImageByProductId(
            @RequestParam Long productId, @RequestParam(defaultValue = "false", required = false) Boolean squaredShapedOnly){
        try {
            Map<String,Object> response = new HashMap<>();
            List<ProductImage> images = repository.findAllByProduct_Id(productId);
            System.out.println(images.size());
            if(images.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if(squaredShapedOnly){
                int j=0;
                for (int i=0;i<images.size()+j;i++){
                    ProductImage image = images.get(i);
                    if(image.getHeight()!=image.getWidth()){
                        images.remove(i-j);
                        j++;
                    }
                }
            }
            response.put("images",images);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value="/productimages", consumes={"application/json"})
    public ResponseEntity<Map<String,Object>> insert(@Valid @RequestBody ProductImageDTO imageDTO){
        try {
            Map<String,Object> response = new HashMap<>();
            Optional<Product> optional = productRepository.findById(imageDTO.getProductId());
            Product product = optional.get();
            if (product==null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setSource(imageDTO.getSource());
            image.setPriority(imageDTO.getPriority());
            image.setHeight(imageDTO.getHeight());
            image.setWidth(imageDTO.getWidth());
            image = repository.save(image);
            response.put("productimage",image);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
