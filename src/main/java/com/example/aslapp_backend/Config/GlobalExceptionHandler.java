package com.example.aslapp_backend.Config;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    @ExceptionHandler(value = RuntimeException.class)
//    public ResponseEntity<Map<String,String>> handleBaseException(RuntimeException e){
//        Map<String,String> map = new HashMap<>();
//        map.put("message",e.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
//    }
//
//}