package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Order;
import com.tmallspringboot.demo.pojo.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
}
