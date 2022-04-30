package com.iozeta.SpringIOZeta;

import com.iozeta.SpringIOZeta.Controllers.utilities.EntranceCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringIoZetaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringIoZetaApplication.class, args);
        System.out.println("Hello world!");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    EntranceCodeGenerator entranceCodeGenerator() {
        return new EntranceCodeGenerator();
    }
}
