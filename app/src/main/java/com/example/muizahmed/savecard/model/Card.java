package com.example.muizahmed.savecard.model;

public class Card{

    private String name;
    private String PhoneNo;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Card(){
    }

    public Card(int id, String name, String phoneNo) {
        this.id = id;
        this.name = name;
        PhoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }



    @Override
    public String toString() {
        String string = "Name: " + name + "\n" +
                "Phone Number: " + PhoneNo;

    return string;
    }
}
