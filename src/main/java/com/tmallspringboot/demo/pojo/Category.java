package com.tmallspringboot.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "category")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
// jpa默认使用hibernate, 会使用proxy模式创建此类的子类来代替且生成 handler 和 hibernateLazyInitializer两个field
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    String name;

    @Transient
    List<Product> products;
    @Transient
    List<List<Product>> productsByRow; // for front page multi list of product list
}
