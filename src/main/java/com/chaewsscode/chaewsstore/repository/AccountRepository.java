package com.chaewsscode.chaewsstore.repository;

import com.chaewsscode.chaewsstore.domain.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Boolean existsByUsername(String username);

    Optional<Account> findByUsername(String username);

}
