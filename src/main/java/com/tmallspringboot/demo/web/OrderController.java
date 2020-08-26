package com.tmallspringboot.demo.web;

import com.tmallspringboot.demo.pojo.Order;
import com.tmallspringboot.demo.service.OrderItemService;
import com.tmallspringboot.demo.service.OrderService;
import com.tmallspringboot.demo.util.Page4Navigator;
import com.tmallspringboot.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start", defaultValue = "0") int start,
                                      @RequestParam(value = "size", defaultValue = "5") int size)
            throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Order> page = orderService.list(start, size, 5);
        for (var order : page.getContent()) {
            orderItemService.fill(order);
        }
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }

    @PutMapping("deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable int oid) throws IOException {
        Order o = orderService.get(oid);
        o.setDeliverDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return Result.success();
    }
}
