package com.globalitians.employees.faculty.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FacultyDetailsModel{

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("faculty_detail")
    @Expose
    private FacultyDetail facultyDetail;

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

    public FacultyDetail getFacultyDetail() {
        return facultyDetail;
    }

    public void setFacultyDetail(FacultyDetail facultyDetail) {
        this.facultyDetail = facultyDetail;
    }

    public class FacultyDetail {

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
        @SerializedName("course_id")
        @Expose
        private String courseId;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("courses")
        @Expose
        private ArrayList<Course> courses = null;
        @SerializedName("standard_id")
        @Expose
        private String standardId;
        @SerializedName("standards")
        @Expose
        private ArrayList<Standard> standards = null;
        @SerializedName("branch_name")
        @Expose
        private String branchName;
        @SerializedName("edited_by")
        @Expose
        private Integer editedBy;
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

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public ArrayList<Course> getCourses() {
            return courses;
        }

        public void setCourses(ArrayList<Course> courses) {
            this.courses = courses;
        }

        public String getStandardId() {
            return standardId;
        }

        public void setStandardId(String standardId) {
            this.standardId = standardId;
        }

        public ArrayList<Standard> getStandards() {
            return standards;
        }

        public void setStandards(ArrayList<Standard> standards) {
            this.standards = standards;
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

        public class Course {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("image")
            @Expose
            private String image;

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

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

        }

        public class Standard {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;

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
