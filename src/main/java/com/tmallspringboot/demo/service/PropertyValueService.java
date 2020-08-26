package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.PropertyValueDAO;
import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.pojo.Property;
import com.tmallspringboot.demo.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyValueService {
    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    public void update(PropertyValue propertyValue) {
        propertyValueDAO.save(propertyValue);
    }

    public void init(Product product) {
        var properties = propertyService.listByCategory(product.getCategory());
        for (var property : properties) {
            var propertyValue = getByPropertyAndProduct(product, property);
            if (propertyValue == null) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }
}
