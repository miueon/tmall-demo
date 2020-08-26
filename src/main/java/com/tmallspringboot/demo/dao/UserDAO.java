package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {

}
