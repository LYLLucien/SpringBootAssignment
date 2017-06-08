package com.lucien;

import com.lucien.config.JpaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Created by Lucien on 2017/6/8.
 */
@Import(JpaConfiguration.class)
@SpringBootApplication(scanBasePackages = {"com.lucien"})
// same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
