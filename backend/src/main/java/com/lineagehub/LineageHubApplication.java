package com.lineagehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LineageHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(LineageHubApplication.class, args);
    }

}
