package com.chaewsscode.chaewsstore.repository;

import com.chaewsscode.chaewsstore.domain.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

}
