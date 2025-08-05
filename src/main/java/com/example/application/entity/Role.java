package com.example.application.entity;

public class Role {
    private String role;

    private String name;

    public void setDetails(String role, String name){
        this.role = role;
        this.name = name;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
