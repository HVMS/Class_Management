package com.globalitians.employees.multiple_attendance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MultipleAttendanceListModelClass {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("batches")
    @Expose
    private ArrayList<Batch> batches = null;

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

    public ArrayList<Batch> getBatches() {
        return batches;
    }

    public void setBatches(ArrayList<Batch> batches) {
        this.batches = batches;
    }

    public class Batch {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("alias")
        @Expose
        private String alias;
        @SerializedName("branch")
        @Expose
        private String branch;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("is_attandence_taken")
        @Expose
        private Boolean isAttandenceTaken;
        @SerializedName("created_at")
        @Expose
        private String createdAt;

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

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getIsAttandenceTaken() {
            return isAttandenceTaken;
        }

        public void setIsAttandenceTaken(Boolean isAttandenceTaken) {
            this.isAttandenceTaken = isAttandenceTaken;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

    }

}
