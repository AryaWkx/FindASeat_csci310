package com.example.findaseat_csci310;

import java.util.Vector;

public class User {
    public String name;
    public String usc_id;
    public String email;
    public String affiliation;
    public String pwd;

    User(String n, String id, String e, String a, String p) {
        name = n;
        usc_id = id;
        email = e;
        affiliation = a;
        pwd = p;
    }

    public void setName(String n) {
        name = n;
    }
    public void setUsc_id(String id) {
        usc_id = id;
    }
    public void setEmail(String e) {
        email = e;
    }
    public void setAffiliation(String a) {
        affiliation = a;
    }
    public void setPwd(String p) {
        pwd = p;
    }
}
