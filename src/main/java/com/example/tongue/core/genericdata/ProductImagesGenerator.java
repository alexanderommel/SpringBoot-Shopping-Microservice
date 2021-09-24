package com.example.tongue.core.genericdata;

import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.ProductImage;
import com.example.tongue.merchants.repositories.ProductImageRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductImagesGenerator {

    public static ArrayList<ProductImage> createImages(ProductImageRepository imageRepository,
                                                       List<Product> productList, String[] sources){


        ArrayList<ProductImage> images = new ArrayList<>();
        for (int i=0;i< productList.size();i++){
            //if (i>=2){
              //  continue;
            //}
            System.out.println("Creating image "+i);
            ProductImage image = new ProductImage();
            image.setSource(sources[i]);
            image.setPriority(1);
            image.setProduct(productList.get(i));
            image = imageRepository.save(image);
            images.add(image);
        }
        return images;
    }


}
