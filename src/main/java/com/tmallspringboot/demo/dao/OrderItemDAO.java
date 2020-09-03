package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Order;
import com.tmallspringboot.demo.pojo.OrderItem;
import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderOrderByIdDesc(Order order);

    List<OrderItem> findByProduct(Product product);

    List<OrderItem> findByUserAndOrderIsNull(User user);
}
