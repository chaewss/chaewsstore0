package com.chaewsscode.chaewsstore.repository;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByIsSoldIsFalse(Pageable pageable);

    Page<Product> findAllByAccount(Account account, Pageable pageable);

}
