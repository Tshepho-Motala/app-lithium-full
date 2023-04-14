package com.example.servicegamesmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

@EnableSidecar
@SpringBootApplication
public class ServiceGamesMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGamesMockApplication.class, args);
    }

}