package com.teranvet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.teranvet"})
public class TeranvetApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TeranvetApplication.class, args);
    }
}
