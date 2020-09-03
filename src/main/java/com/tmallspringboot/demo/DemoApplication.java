package com.tmallspringboot.demo;

import com.tmallspringboot.demo.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // reids cacheing
public class DemoApplication {
    static {
        PortUtil.checkPort(6379, "Redis server", true);
    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
