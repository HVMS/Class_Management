package com.globalitians.employees;

public class MonthModel {

    public String strMonthName = "";
    public int monthIndex = 0;
    public boolean isSelected = false;

    public MonthModel(String strMonthName,
                      int monthIndex,
                      boolean isSelected) {
        this.isSelected = isSelected;
        this.monthIndex = monthIndex;
        this.strMonthName = strMonthName;
    }
}
