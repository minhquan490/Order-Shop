package com.bachlinh.order.web;

import com.bachlinh.order.aot.GlobalReflectiveRuntimeHint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints(GlobalReflectiveRuntimeHint.class)
public class OrderShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderShopApplication.class, args);
    }
}
