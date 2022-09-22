package com.chaewsscode.chaewsstore.repository;

import com.chaewsscode.chaewsstore.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
