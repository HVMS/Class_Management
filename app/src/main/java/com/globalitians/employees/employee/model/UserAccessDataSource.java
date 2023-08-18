package com.globalitians.employees.employee.model;

public class UserAccessDataSource {
    private static UserAccessDataSource instance = new UserAccessDataSource();

    // your json data model
    private EmployeeLoginModel data;

   // private constructor to prevent creating new instances
    private UserAccessDataSource() {
    }

    public static UserAccessDataSource getInstance() {
        return instance;
    }

    public EmployeeLoginModel getData() {
        return data;
    }

    public void setData(EmployeeLoginModel data) {
        this.data = data;
    }
}