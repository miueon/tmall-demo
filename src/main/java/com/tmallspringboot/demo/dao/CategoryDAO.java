package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDAO extends JpaRepository<Category, Integer> {

}
