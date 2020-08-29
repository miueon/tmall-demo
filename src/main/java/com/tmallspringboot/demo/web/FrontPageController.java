package com.tmallspringboot.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class FrontPageController {
    @GetMapping(value = "/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping(value = "/home")
    public String home() {
        return "front/home";
    }

    @GetMapping(value="/register")
    public String register(){
        return "front/register";
    }
    @GetMapping(value="/alipay")
    public String alipay(){
        return "front/alipay";
    }
    @GetMapping(value="/bought")
    public String bought(){
        return "front/bought";
    }
    @GetMapping(value="/buy")
    public String buy(){
        return "front/buy";
    }
    @GetMapping(value="/cart")
    public String cart(){
        return "front/cart";
    }
    @GetMapping(value="/category")
    public String category(){
        return "front/category";
    }
    @GetMapping(value="/confirmPay")
    public String confirmPay(){
        return "front/confirmPay";
    }
    @GetMapping(value="/login")
    public String login(){
        return "front/login";
    }
    @GetMapping(value="/orderConfirmed")
    public String orderConfirmed(){
        return "front/orderConfirmed";
    }
    @GetMapping(value="/payed")
    public String payed(){
        return "front/payed";
    }
    @GetMapping(value="/product")
    public String product(){
        return "front/product";
    }
    @GetMapping(value="/registerSuccess")
    public String registerSuccess(){
        return "front/registerSuccess";
    }
    @GetMapping(value="/review")
    public String review(){
        return "front/review";
    }
    @GetMapping(value="/search")
    public String searchResult(){
        return "front/search";
    }
    @GetMapping("/frontlogout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:home";
    }

}
