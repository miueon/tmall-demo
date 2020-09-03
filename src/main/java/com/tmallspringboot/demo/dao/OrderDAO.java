package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Order;
import com.tmallspringboot.demo.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order, Integer> {
    // return order for specific user and status is not "delete"
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
