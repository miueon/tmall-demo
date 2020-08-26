package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Category;
import com.tmallspringboot.demo.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyDAO extends JpaRepository<Property, Integer> {
    Page<Property> findByCategory(Category category, Pageable pageable);
    // this will use category as cid, Pageable as group by

    List<Property> findByCategory(Category category);
}
