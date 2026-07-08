package com.travelplanner.demo.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "User_TBL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column(name = "USER_ID", length = 20)
    private String userId;

    @Column(name = "PASSWORD", length = 100, nullable = false)
    private String password;

    @Column(name = "NAME", length = 20, nullable = false)
    private String name;
}