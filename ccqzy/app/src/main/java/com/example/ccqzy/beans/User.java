package com.example.ccqzy.beans;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/6.
 * 用户信息
 *
 */
@Table(name = "userInfo")
public class User implements Serializable {

    @Column(name = "id",isId = true)
    private String id;

    @Column(name = "TenancyName")
    private String TenancyName;

    @Column(name = "UsernameOrEmailAddress")
    private String UsernameOrEmailAddress;

    @Column(name = "Password")
    private String Password;

    public User() {

    }

    public User(String id, String tenancyName, String usernameOrEmailAddress, String password) {
        this.id = id;
        TenancyName = tenancyName;
        UsernameOrEmailAddress = usernameOrEmailAddress;
        Password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenancyName() {
        return TenancyName;
    }

    public void setTenancyName(String tenancyName) {
        TenancyName = tenancyName;
    }

    public String getUsernameOrEmailAddress() {
        return UsernameOrEmailAddress;
    }

    public void setUsernameOrEmailAddress(String usernameOrEmailAddress) {
        UsernameOrEmailAddress = usernameOrEmailAddress;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", TenancyName='" + TenancyName + '\'' +
                ", UsernameOrEmailAddress='" + UsernameOrEmailAddress + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }
}
