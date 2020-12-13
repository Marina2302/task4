package com.buinevich.task4.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    int id;
    String name;
    String mail;
    LocalDateTime regDate;
    LocalDateTime lastLoginDate;
    String status;
}


