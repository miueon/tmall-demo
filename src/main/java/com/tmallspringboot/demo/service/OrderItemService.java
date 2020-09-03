package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.OrderDAO;
import com.tmallspringboot.demo.dao.OrderItemDAO;
import com.tmallspringboot.demo.pojo.Order;
import com.tmallspringboot.demo.pojo.OrderItem;
import com.tmallspringboot.demo.pojo.Product;
import com.tmallspringboot.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi : orderItems) {
            total += oi.getNumber() * oi.getProduct().getPromotedPrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
    }

    public List<OrderItem> listByOrder(Order order) {

        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    public OrderItem get(int id) {
        return orderItemDAO.findById(id).orElse(null);
    }

    public void delete(int id) {
        orderItemDAO.deleteById(id);
    }

    public int getSaleCount(Product product) {
        List<OrderItem> ois = listByProduct(product);
        int result = 0;
        for (OrderItem oi : ois) {
                if (null != oi.getOrder() && null != oi.getOrder().getPayDate())
                    result += oi.getNumber();
        }
        return result;
    }

    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    public List<OrderItem> listByUser(User user) {
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }


}
