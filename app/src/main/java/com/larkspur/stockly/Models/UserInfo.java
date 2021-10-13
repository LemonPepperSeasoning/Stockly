package com.larkspur.stockly.Models;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {


    private static UserInfo single_instance = null;
    private String _username;

    private UserInfo() {
        _username = "Guest";
    }

    public static UserInfo getInstance() {
        if (single_instance == null) {
            single_instance = new UserInfo();
        }
        return single_instance;
    }

    public void setUsername(String username){
        _username = username;
    }

    public String getUsername(){
        return _username;
    }

}
