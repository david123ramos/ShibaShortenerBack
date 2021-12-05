package com.shibashortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShibashortnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShibashortnerApplication.class, args);
    }

}
