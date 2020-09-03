package com.tmallspringboot.demo.interceptor;

import com.tmallspringboot.demo.pojo.User;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        val session = request.getSession();
        val contextPath = session.getServletContext().getContextPath();

        String[] requireAuthPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",

                "frontbuyone",
                "frontbuy",
                "frontaddCart",
                "frontcart",
                "frontchangeOrderItem",
                "frontdeleteOrderItem",
                "frontcreateOrder",
                "frontpayed",
                "frontbought",
                "frontconfirmPay",
                "frontorderConfirmed",
                "frontdeleteOrder",
                "frontreview",
                "frontdoreview"

        };

        var uri = request.getRequestURI();
        uri = StringUtils.remove(uri, contextPath + "/");
        String page = uri;

        if (beginWith(page, requireAuthPages)) {
            //var usr = (User) session.getAttribute("user");
//            if (usr == null) {
//                response.sendRedirect("login");
//                return false;
//            }
            val subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                response.sendRedirect("login");
                return false;
            }
        }
        return true;
    }

    private boolean beginWith(String page, String[] requireAuthPages) {
        var result = false;
        for (var requiredPage : requireAuthPages) {
            if (StringUtils.startsWith(page, requiredPage)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
