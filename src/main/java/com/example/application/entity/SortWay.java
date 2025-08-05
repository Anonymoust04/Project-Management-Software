package com.example.application.entity;

public class SortWay {

    private String currentSortMethod;

    private Boolean isAscending;

    public void setDetails(String currentSortMethod, Boolean isAscending){
        this.currentSortMethod = currentSortMethod;
        this.isAscending = isAscending;
    }

    public void setCurrentSortMethod(String currentSortMethod){
        this.currentSortMethod = currentSortMethod;
    }

    public String getCurrentSortMethod(){
        return currentSortMethod;
    }

    public void setIsAscending(Boolean isAscending){
        this.isAscending = isAscending;
    }

    public Boolean getIsAscending(){
        return isAscending;
    }
}
