package com.example.cashwalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CashwalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(CashwalkApplication.class, args);
    }
}
