package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.CategoryDAO;
import com.tmallspringboot.demo.pojo.Category;
import com.tmallspringboot.demo.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    public List<Category> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start,size , sort); // Pageable defines how much entity to show
        Page pageFromJPA = categoryDAO.findAll(pageable);// require pages from categoryDAO by pageable
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    public void delete(int id) {
        categoryDAO.deleteById(id);
    }

    public Category get(int id) {
        Optional<Category> c = categoryDAO.findById(id);
        return c.orElse(null);
    }

    public void update(Category bean) {
        categoryDAO.save(bean);
    }
}