package com.globalitians.employees.filters.models;

public class FilterModelMonths {
    private String strMonth="";
    private String monthNumber="";
    private String strYear="";
    private boolean isSelected=false;

    public String getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(String monthNumber) {
        this.monthNumber = monthNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStrMonth() {
        return strMonth;
    }

    public void setStrMonth(String strMonth) {
        this.strMonth = strMonth;
    }

    public String getStrYear() {
        return strYear;
    }

    public void setStrYear(String strYear) {
        this.strYear = strYear;
    }
}
