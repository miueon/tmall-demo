package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDAO extends JpaRepository<Order, Integer> {

}
