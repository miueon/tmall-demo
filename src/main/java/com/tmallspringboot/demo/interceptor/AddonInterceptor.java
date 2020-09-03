package com.tmallspringboot.demo.interceptor;

import com.tmallspringboot.demo.pojo.OrderItem;
import com.tmallspringboot.demo.pojo.User;
import com.tmallspringboot.demo.service.CategoryService;
import com.tmallspringboot.demo.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddonInterceptor implements HandlerInterceptor {
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        var session = request.getSession();
        var usr = (User) session.getAttribute("user");
        int cartTotalItemNum = 0;
        if (usr != null) {
            var ois = orderItemService.listByUser(usr);
            cartTotalItemNum = ois.parallelStream()
                    .mapToInt(OrderItem::getNumber).sum();
        }
        var cs = categoryService.list();
        var contextPath = request.getServletContext().getContextPath();

        request.getServletContext().setAttribute("categories_below_search", cs);
        session.setAttribute("cartTotalItemNumber", cartTotalItemNum);
        request.getServletContext().setAttribute("contextPath", contextPath);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
