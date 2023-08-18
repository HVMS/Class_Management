package com.globalitians.employees.filters.models;

public class FilterModelStudentTypes {
    private String strStudentType="";
    private boolean isSelected=false;

    public String getStrStudentType() {
        return strStudentType;
    }

    public void setStrStudentType(String strStudentType) {
        this.strStudentType = strStudentType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
