package com.tmallspringboot.demo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
@Data
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String password;
    private String name;
    private String salt;

    @Transient
    private String anonymousName;

    public String getAnonymousName() {
        if (null != anonymousName)
            return anonymousName;
        if (null == name) {
            anonymousName = null;
        } else if (name.length() <= 1)
            anonymousName = "*";
        else if (name.length() == 2)
            anonymousName = name.substring(0, 1) + "*";
        else {
            char[] cs = name.toCharArray();
            for (int i = 1; i < cs.length - 1; i++) {
                cs[i] = '*';
            }
            anonymousName = new String(cs);
        }
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }
}
