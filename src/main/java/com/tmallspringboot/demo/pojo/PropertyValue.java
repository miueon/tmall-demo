package com.tmallspringboot.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "propertyvalue")
@Data
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class PropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "pid")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ptid")
    private Property property;

    private String value;
    @Override
    public String toString() {
        return "PropertyValue [id=" + id + ", product=" + product + ", property=" + property +
                ", value=" + value + "]";
    }
}
