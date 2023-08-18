package com.globalitians.employees.multiple_attendance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MultipleAttendanceDetailsModelClass {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("batch_detail")
    @Expose
    private BatchDetail batchDetail;

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

    public BatchDetail getBatchDetail() {
        return batchDetail;
    }

    public void setBatchDetail(BatchDetail batchDetail) {
        this.batchDetail = batchDetail;
    }

    public class BatchDetail {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("alias")
        @Expose
        private String alias;
        @SerializedName("branch_id")
        @Expose
        private Integer branchId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("branch_name")
        @Expose
        private String branchName;
        @SerializedName("is_attandence_taken")
        @Expose
        private Boolean isAttandenceTaken;
        @SerializedName("students")
        @Expose
        private ArrayList<Student> students = null;

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

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Integer getBranchId() {
            return branchId;
        }

        public void setBranchId(Integer branchId) {
            this.branchId = branchId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public Boolean getIsAttandenceTaken() {
            return isAttandenceTaken;
        }

        public void setIsAttandenceTaken(Boolean isAttandenceTaken) {
            this.isAttandenceTaken = isAttandenceTaken;
        }

        public ArrayList<Student> getStudents() {
            return students;
        }

        public void setStudents(ArrayList<Student> students) {
            this.students = students;
        }

        public class Student {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("first_name")
            @Expose
            private String firstName;
            @SerializedName("last_name")
            @Expose
            private String lastName;
            @SerializedName("image")
            @Expose
            private String image;
            @SerializedName("att_date")
            @Expose
            private String attDate;
            @SerializedName("att_status")
            @Expose
            private String attStatus;
            @SerializedName("att_intime")
            @Expose
            private String attIntime;

            private boolean isSelected = false;

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                this.isSelected = selected;
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getAttDate() {
                return attDate;
            }

            public void setAttDate(String attDate) {
                this.attDate = attDate;
            }

            public String getAttStatus() {
                return attStatus;
            }

            public void setAttStatus(String attStatus) {
                this.attStatus = attStatus;
            }

            public String getAttIntime() {
                return attIntime;
            }

            public void setAttIntime(String attIntime) {
                this.attIntime = attIntime;
            }

        }

    }

}
