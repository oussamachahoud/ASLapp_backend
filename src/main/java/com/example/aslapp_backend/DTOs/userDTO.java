package com.example.aslapp_backend.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class userDTO {
    private Long id;
    private String username;
    private String email;
    private int age;
    private String imageURL;
}