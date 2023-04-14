package com.example.servicedomainmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

@EnableSidecar
@SpringBootApplication
public class ServiceDomainMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDomainMockApplication.class, args);
    }

}
