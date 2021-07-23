package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findAllByProduct_Id(Long id);
    List<ProductImage> findAllByProduct_IdAndHeightIsLessThanEqualAndWidthIsLessThanEqual(Long id,int height,int width);
}
