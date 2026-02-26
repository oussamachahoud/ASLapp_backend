package com.example.aslapp_backend;


import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UserTest {


    public static void main (String[] args) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] keyBytes = digest.digest("oussama".getBytes(StandardCharsets.UTF_8));

        System.out.println(Base64.getEncoder().encodeToString(keyBytes));
    }
}