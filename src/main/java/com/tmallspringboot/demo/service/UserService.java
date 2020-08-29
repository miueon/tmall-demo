package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.UserDAO;
import com.tmallspringboot.demo.pojo.User;
import com.tmallspringboot.demo.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    public Page4Navigator<User> list(int start, int size, int navigatePages) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        Page pageFromJpa = userDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa, navigatePages);
    }

    public boolean isExist(String name) {
        User user = getByName(name);
        return null!=user;
    }

    public User getByName(String name) {
        return userDAO.findByName(name);
    }

    public void add(User user) {
        userDAO.save(user);
    }
    public User get(String name, String password) {
        return userDAO.getByNameAndPassword(name,password);
    }

}
