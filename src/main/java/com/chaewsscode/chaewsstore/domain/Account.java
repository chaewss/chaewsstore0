package com.chaewsscode.chaewsstore.domain;

import com.chaewsscode.chaewsstore.util.Authority;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Column(unique = true)
    private String nickname;

    @NotNull
    private String phone;

    @NotNull
    private String email;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    public void setPassword(String password) {
        this.password = password;
    }

}
