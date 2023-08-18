package com.globalitians.employees.inquiry.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelInquiry {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("inquiries")
    @Expose
    private ArrayList<Inquiry> inquiries = null;

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

    public ArrayList<Inquiry> getInquiries() {
        return inquiries;
    }

    public void setInquiries(ArrayList<Inquiry> inquiries) {
        this.inquiries = inquiries;
    }
    public class Inquiry {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("fname")
        @Expose
        private String fname;
        @SerializedName("lname")
        @Expose
        private String lname;
        @SerializedName("contact")
        @Expose
        private String contact;
        @SerializedName("inquiry_date")
        @Expose
        private String inquiryDate;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("slug")
        @Expose
        private String slug;
        @SerializedName("courses")
        @Expose
        private ArrayList<Course> courses = null;

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

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getInquiryDate() {
            return inquiryDate;
        }

        public void setInquiryDate(String inquiryDate) {
            this.inquiryDate = inquiryDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public ArrayList<Course> getCourses() {
            return courses;
        }

        public void setCourses(ArrayList<Course> courses) {
            this.courses = courses;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }
    }


    public class Course {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
