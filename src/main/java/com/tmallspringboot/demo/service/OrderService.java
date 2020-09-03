package com.tmallspringboot.demo.service;

import com.tmallspringboot.demo.dao.OrderDAO;
import com.tmallspringboot.demo.pojo.Order;
import com.tmallspringboot.demo.pojo.OrderItem;
import com.tmallspringboot.demo.pojo.User;
import com.tmallspringboot.demo.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    @Autowired
    OrderItemService orderItemService;

    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        Page pageFromJPA = orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }

    // in order to integrate redis, we can't use @JsonIgnoreProperties
    //
    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public float add(Order order, List<OrderItem> orderItemList) {
        AtomicReference<Float> total = new AtomicReference<>((float) 0);
        add(order);

        orderItemList.forEach(orderItem -> {
                    orderItem.setOrder(order);
                    orderItemService.update(orderItem);
                    total.updateAndGet(
                            v -> (float)
                                    (v + orderItem.getProduct().getPromotedPrice() *
                                            orderItem.getNumber()));
                });
        return total.get();
    }

    private void add(Order order) {
        orderDAO.save(order);
    }

    public List<Order> listByUserAndNotDeleted(User user) {
        var os = orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
        orderItemService.fill(os);
        return os;
    }

    public void calc(Order o) {
        var ois = o.getOrderItems();
        float total  = ois.parallelStream().map(
                orderItem -> orderItem.getProduct()
                        .getPromotedPrice() * orderItem.getNumber())
                .reduce((float) 0, Float::sum);
        o.setTotal(total);
    }
}
