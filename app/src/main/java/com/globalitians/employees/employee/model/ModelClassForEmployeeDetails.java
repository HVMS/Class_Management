package com.globalitians.employees.employee.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelClassForEmployeeDetails {

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
        @SerializedName("branch_id")
        @Expose
        private Integer branchId;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("job_type")
        @Expose
        private String jobType;
        @SerializedName("salary")
        @Expose
        private String salary;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("designation")
        @Expose
        private String designation;
        @SerializedName("contact_no")
        @Expose
        private String contactNo;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("branch_name")
        @Expose
        private String branchName;
        @SerializedName("edited_by")
        @Expose
        private Integer editedBy;
        @SerializedName("edited_user_name")
        @Expose
        private String editedUserName;
        @SerializedName("dob")
        @Expose
        private String dob;
        @SerializedName("joining_date")
        @Expose
        private String joiningDate;
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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Integer getBranchId() {
            return branchId;
        }

        public void setBranchId(Integer branchId) {
            this.branchId = branchId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getJobType() {
            return jobType;
        }

        public void setJobType(String jobType) {
            this.jobType = jobType;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public Integer getEditedBy() {
            return editedBy;
        }

        public void setEditedBy(Integer editedBy) {
            this.editedBy = editedBy;
        }

        public String getEditedUserName() {
            return editedUserName;
        }

        public void setEditedUserName(String editedUserName) {
            this.editedUserName = editedUserName;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getJoiningDate() {
            return joiningDate;
        }

        public void setJoiningDate(String joiningDate) {
            this.joiningDate = joiningDate;
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
