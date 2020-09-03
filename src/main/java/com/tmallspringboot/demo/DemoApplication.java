package com.tmallspringboot.demo;

import com.tmallspringboot.demo.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching // reids cacheing
@EnableElasticsearchRepositories(basePackages = "com.tmallspringboot.demo.es")
@EnableJpaRepositories(basePackages = {"com.tmallspringboot.demo.pojo", "com.tmallspringboot.demo.dao"})
public class DemoApplication {
    static {
        PortUtil.checkPort(6379, "Redis server", true);
        PortUtil.checkPort(9300, "ElasticSearch", true);
        PortUtil.checkPort(5601, "Kibana", true);
    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
