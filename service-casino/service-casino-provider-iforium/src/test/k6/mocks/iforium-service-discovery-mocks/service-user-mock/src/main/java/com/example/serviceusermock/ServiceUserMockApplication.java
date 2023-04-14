package com.example.serviceusermock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

@EnableSidecar
@SpringBootApplication
public class ServiceUserMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserMockApplication.class, args);
    }

}