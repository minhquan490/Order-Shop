package com.bachlinh.order.web;

import com.bachlinh.order.annotation.SpringApplication;
import com.bachlinh.order.aot.GlobalReflectiveRuntimeHint;
import com.bachlinh.order.core.Application;
import com.bachlinh.order.web.servlet.WebServlet;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringApplication
@ImportRuntimeHints(GlobalReflectiveRuntimeHint.class)
@Import({WebServlet.class})
public class OrderShopApplication {

    public static void main(String[] args) {
        Application.run(OrderShopApplication.class, args);
    }
}
