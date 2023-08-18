package com.globalitians.employees.Homework.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HomeWorkDetailsModelClass {

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
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("batch_id")
        @Expose
        private Integer batchId;
        @SerializedName("batch_name")
        @Expose
        private String batchName;
        @SerializedName("attachments")
        @Expose
        private ArrayList<Attachment> attachments = null;
        @SerializedName("edited_user")
        @Expose
        private Integer editedUser;
        @SerializedName("user_name")
        @Expose
        private String userName;
        @SerializedName("students")
        @Expose
        private ArrayList<Student> students = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getBatchId() {
            return batchId;
        }

        public void setBatchId(Integer batchId) {
            this.batchId = batchId;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
        }

        public ArrayList<Attachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(ArrayList<Attachment> attachments) {
            this.attachments = attachments;
        }

        public Integer getEditedUser() {
            return editedUser;
        }

        public void setEditedUser(Integer editedUser) {
            this.editedUser = editedUser;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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
            @SerializedName("fname")
            @Expose
            private String fname;
            @SerializedName("lname")
            @Expose
            private String lname;
            @SerializedName("homework_status")
            @Expose
            private Object homeworkStatus;
            @SerializedName("image")
            @Expose
            private String image;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getFname() {
                return fname;
            }

            public void setFname(String fname) {
                this.fname = fname;
            }

            public String getLname() {
                return lname;
            }

            public void setLname(String lname) {
                this.lname = lname;
            }

            public Object getHomeworkStatus() {
                return homeworkStatus;
            }

            public void setHomeworkStatus(Object homeworkStatus) {
                this.homeworkStatus = homeworkStatus;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

        }

        public class Attachment {

            @SerializedName("image")
            @Expose
            private String image;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

        }

    }
}
