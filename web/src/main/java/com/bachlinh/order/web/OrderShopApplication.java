package com.bachlinh.order.web;

import com.bachlinh.order.annotation.SpringApplication;
import com.bachlinh.order.aot.GlobalReflectiveRuntimeHint;
import com.bachlinh.order.core.Application;

@SpringApplication(runtimeHints = GlobalReflectiveRuntimeHint.class)
public class OrderShopApplication {

    public static void main(String[] args) {
        Application.run(OrderShopApplication.class, args);
    }
}
