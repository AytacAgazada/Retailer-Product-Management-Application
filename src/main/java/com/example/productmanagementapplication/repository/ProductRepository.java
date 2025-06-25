package com.example.productmanagementapplication.repository;

import com.example.productmanagementapplication.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {


}
