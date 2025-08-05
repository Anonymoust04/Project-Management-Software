package com.example.application.entity;

import java.util.ArrayList;

public class Filter {
    private ArrayList<String> filterList;

    public void setFilterList(ArrayList<String> filterList){
        this.filterList = filterList;
    }

    public ArrayList<String> getFilterList(){
        return filterList;
    }
}

