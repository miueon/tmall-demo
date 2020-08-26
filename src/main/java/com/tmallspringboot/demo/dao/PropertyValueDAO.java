package com.tmallspringboot.demo.dao;

import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.pojo.Property;
import com.tmallspringboot.demo.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    PropertyValue getByPropertyAndProduct(Property property, Product product);
    // this two will map into pid and ptid in the table
}
