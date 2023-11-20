package com.example.findaseat_csci310;

import java.util.ArrayList;
import java.util.Vector;

public class User {
    public String name;
    public String usc_id;
    public String email;
    public String affiliation;
    public String pwd;

    public ArrayList<Reservation> history;
    public Reservation currentReservation;

    User() {
        name = null;
        usc_id = null;
        email = null;
        affiliation = null;
        pwd = null;
        history = new ArrayList<Reservation>();
        currentReservation = null;
    }

    User(String e, String id, String n, String a, String p) {
        name = n;
        usc_id = id;
        email = e;
        affiliation = a;
        pwd = p;
        history = new ArrayList<Reservation>();
        currentReservation = null;
    }

    public String getName() {
        return name;
    }

    public String getUsc_id() {
        return usc_id;
    }

    public String getEmail() {
        return email;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getPwd() {
        return pwd;
    }

}
