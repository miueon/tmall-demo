package com.tmallspringboot.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "property")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "cid")
    private Category category;

    @Override
    public String toString() {
        return "Property [id=" + id + ", name=" + name + ", category=" + category + "]";
    }
}
