package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Category;
import com.tmallspringboot.demo.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDAO extends JpaRepository<Product, Integer> {
    Page<Product> findByCategory(Category category, Pageable pageable);

}