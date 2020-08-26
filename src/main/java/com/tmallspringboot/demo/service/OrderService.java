package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.OrderDAO;
import com.tmallspringboot.demo.pojo.Order;
import com.tmallspringboot.demo.pojo.OrderItem;
import com.tmallspringboot.demo.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDAO orderDAO;

    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort =  Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable =  PageRequest.of(start, size,sort);
        Page pageFromJPA =orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }

    // in order to integrate redis, we can't use @JsonIgnoreProperties
    //
    private void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems= order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }

    public Order get(int oid) {
        return orderDAO.findById(oid).orElse(null);
    }

    public void update(Order bean) {
        orderDAO.save(bean);
    }
}
