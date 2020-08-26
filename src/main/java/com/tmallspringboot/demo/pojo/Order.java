package com.tmallspringboot.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tmallspringboot.demo.service.OrderService;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "order_")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    private String orderCode;
    private String address;
    private String post;
    private String receiver;
    private String mobile;
    private String userMessage;
    private Date createDate;
    private Date payDate;
    private Date deliverDate;
    private Date confirmDate;
    private String status;

    @Transient
    private List<OrderItem> orderItems;
    @Transient
    private float total;
    @Transient
    private int totalNumber;


}
