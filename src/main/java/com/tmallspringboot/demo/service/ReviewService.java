package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.ReviewDAO;
import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    ReviewDAO reviewDAO;
    @Autowired
    ProductService productService;

    public void add(Review review) {
        reviewDAO.save(review);
    }

    public List<Review> list(Product product) {
        return reviewDAO.findByProductOrderByIdDesc(product);
    }

    public int getCount(Product product) {
        return reviewDAO.countByProduct(product);
    }


}
