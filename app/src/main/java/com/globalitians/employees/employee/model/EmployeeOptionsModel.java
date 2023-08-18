package com.globalitians.employees.employee.model;

public class EmployeeOptionsModel {
    private String strOptionTitle;
    private int optionImageId;
    private int backgroundImageId;
    private String strColor;

    public int getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(int backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    public String getStrColor() {
        return strColor;
    }

    public void setStrColor(String strColor) {
        this.strColor = strColor;
    }

    public String getStrOptionTitle() {
        return strOptionTitle;
    }

    public void setStrOptionTitle(String strOptionTitle) {
        this.strOptionTitle = strOptionTitle;
    }

    public int getOptionImageId() {
        return optionImageId;
    }

    public void setOptionImageId(int optionImageId) {
        this.optionImageId = optionImageId;
    }
}
