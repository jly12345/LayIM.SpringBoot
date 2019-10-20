package com.fyp.entity;

import lombok.Data;

@Data
public class User {
    public static final User Anonymous = new User("匿名","");

    public User(){

    }
    public User(String name,String avatar){
        setUsername(name);
        setAvatar(avatar);
    }


    protected Long id;
    protected String avatar;
    protected String username;
    protected String sign;
    protected Long createAt;

}
