package com.globalitians.employees.admin.model;

public class AccessItems {
    private boolean isSelected=false;
    private String name="";

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
