package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findAll(Pageable pageable);
    //Page<ProductCustomerDTO> findAllBy(Pageable pageable);
    Page<Product> findAllByDescription(String description, Pageable pageable);
    Page<Product> findAllByHandle(String handle, Pageable pageable);
    Page<Product> findAllByTitle(String title, Pageable pageable);
    Page<Product> findAllByType(String type, Pageable pageable);
    Page<Product> findAllByStatus(String status, Pageable pageable);
    Page<Product> findAllByTags(String tags, Pageable pageable);
    Page<Product> findAllByInventorId(String inventor_id, Pageable pageable);
    Page<Product> findAllByPrice(Double price, Pageable pageable);
    Page<Product> findAllByOriginalPrice(Pageable pageable, Double originalPrice);
    List<Product> findByIdIn(List<Long> ids);

}
