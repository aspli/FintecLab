package com.bootcamp.conta_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ContaService2025Application {

    public static void main(String[] args) {
        SpringApplication.run(ContaService2025Application.class, args);
    }

}
