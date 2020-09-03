package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDAO extends JpaRepository<Review, Integer> {
    List<Review> findByProductOrderByIdDesc(Product product);

    int countByProduct(Product product);
}
