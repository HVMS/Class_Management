package com.globalitians.employees.employee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelClassForEmployeeList {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("employees")
    @Expose
    private ArrayList<Employee> employees = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<Employee> employees) {
        this.employees = employees;
    }

    public class Employee {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("branch")
        @Expose
        private String branch;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("designation")
        @Expose
        private String designation;
        @SerializedName("access")
        @Expose
        private ArrayList<Access> access = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public ArrayList<Access> getAccess() {
            return access;
        }

        public void setAccess(ArrayList<Access> access) {
            this.access = access;
        }

        public class Access {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("access_items")
            @Expose
            private ArrayList<AccessItem> accessItems = null;
            @SerializedName("access_id")
            @Expose
            private Integer accessId;
            @SerializedName("access_name")
            @Expose
            private String accessName;
            @SerializedName("access_alias")
            @Expose
            private String accessAlias;
            @SerializedName("assign_for")
            @Expose
            private String assignFor;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public ArrayList<AccessItem> getAccessItems() {
                return accessItems;
            }

            public void setAccessItems(ArrayList<AccessItem> accessItems) {
                this.accessItems = accessItems;
            }

            public Integer getAccessId() {
                return accessId;
            }

            public void setAccessId(Integer accessId) {
                this.accessId = accessId;
            }

            public String getAccessName() {
                return accessName;
            }

            public void setAccessName(String accessName) {
                this.accessName = accessName;
            }

            public String getAccessAlias() {
                return accessAlias;
            }

            public void setAccessAlias(String accessAlias) {
                this.accessAlias = accessAlias;
            }

            public String getAssignFor() {
                return assignFor;
            }

            public void setAssignFor(String assignFor) {
                this.assignFor = assignFor;
            }

        }

        public class AccessItem {

            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("is_active")
            @Expose
            private Integer isActive;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getIsActive() {
                return isActive;
            }

            public void setIsActive(Integer isActive) {
                this.isActive = isActive;
            }

        }
    }

}
