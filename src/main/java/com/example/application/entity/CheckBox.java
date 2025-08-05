package com.example.application.entity;

public class CheckBox {
    private String labelElement;
    private Boolean isChecked = false; 

    public void setLabelElement(String labelElement){
        this.labelElement = labelElement;
    }

    public String getLabelElement(){
        return labelElement;
    }

    public void setIsChecked(Boolean isChecked){
        this.isChecked = isChecked;
    }

    public Boolean getIsChecked(){
        return isChecked;
    }
}
