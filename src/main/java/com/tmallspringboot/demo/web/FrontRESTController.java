package com.tmallspringboot.demo.web;

import com.tmallspringboot.demo.pojo.Category;
import com.tmallspringboot.demo.pojo.User;
import com.tmallspringboot.demo.service.CategoryService;
import com.tmallspringboot.demo.service.ProductService;
import com.tmallspringboot.demo.service.UserService;
import com.tmallspringboot.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class FrontRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @GetMapping("/fronthome")
    public List<Category> home() {
        var categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

    @PostMapping("/frontregister")
    public Object register(@RequestBody User user) {
        String name =  user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if(exist){
            String message ="This userName has been used";
            return Result.fail(message);
        }

        user.setPassword(password);

        userService.add(user);

        return Result.success();
    }

    @PostMapping("/frontlogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user = userService.get(name, userParam.getPassword());
        if (user == null) {
            String msg = "User name or password incorrect!";
            return Result.fail(msg);
        } else {
            session.setAttribute("user", user);
            return Result.success();
        }
    }


}
