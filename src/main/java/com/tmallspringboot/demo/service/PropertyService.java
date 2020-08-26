package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.PropertyDAO;
import com.tmallspringboot.demo.pojo.Category;
import com.tmallspringboot.demo.pojo.Property;
import com.tmallspringboot.demo.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;



    public void add(Property property) {
        propertyDAO.save(property);
    }

    public void delete(int id) {
        propertyDAO.deleteById(id);
    }

    public Property get(int id) {
        return propertyDAO.findById(id).orElse(null);
    }

    public void update(Property property) {
        propertyDAO.save(property);
    }

    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        Page<Property> pageFromJpa = propertyDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJpa, navigatePages);
    }

    public List<Property> listByCategory(Category category) {
        return propertyDAO.findByCategory(category);
    }
}
