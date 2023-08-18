package com.globalitians.employees.employee.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EmployeeLoginModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("employee_detail")
    @Expose
    private EmployeeDetail employeeDetail;

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

    public EmployeeDetail getEmployeeDetail() {
        return employeeDetail;
    }

    public void setEmployeeDetail(EmployeeDetail employeeDetail) {
        this.employeeDetail = employeeDetail;
    }

    public class EmployeeDetail {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("access")
        @Expose
        private ArrayList<Access> access = null;
        @SerializedName("branch_id")
        @Expose
        private Integer branchId;

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

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

        public ArrayList<Access> getAccess() {
            return access;
        }

        public void setAccess(ArrayList<Access> access) {
            this.access = access;
        }

        public Integer getBranchId() {
            return branchId;
        }

        public void setBranchId(Integer branchId) {
            this.branchId = branchId;
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
}
