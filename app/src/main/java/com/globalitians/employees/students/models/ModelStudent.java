package com.globalitians.employees.students.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelStudent {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("students")
    @Expose
    private ArrayList<Student> students = null;

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

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public static class Student {

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
        @SerializedName("amount")
        @Expose

        private String amount;

        public String getIn_out_status() {
            return in_out_status;
        }

        public void setIn_out_status(String in_out_status) {
            this.in_out_status = in_out_status;
        }

        @SerializedName("in_out_status")

        @Expose
        private String in_out_status;
        @SerializedName("courses")
        @Expose
        private ArrayList<Course> courses = null;
        @SerializedName("branch_name")
        @Expose
        private String branchName;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("parentname")
        @Expose
        private String parentname;
        @SerializedName("parentmobileno")
        @Expose
        private String parentmobileno;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("reference")
        @Expose
        private String reference;
        @SerializedName("joining_date")
        @Expose
        private String joiningDate;
        @SerializedName("dob")
        @Expose
        private String dob;

        @SerializedName("unpaid_amount")
        @Expose
        private String unpaid_amount;

        @SerializedName("course_status")
        @Expose

        private String course_status;

        @SerializedName("batch_name")
        @Expose

        private String batch_name;

        public String getBatch_name() {
            return batch_name;
        }

        public void setBatch_name(String batch_name) {
            this.batch_name = batch_name;
        }

        private boolean isChecked = false;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            this.isChecked = checked;
        }

        public String getCourse_status() {
            return course_status;
        }

        public void setCourse_status(String course_status) {
            this.course_status = course_status;
        }

        public String getUnpaid_amount() {
            return unpaid_amount;
        }

        public void setUnpaid_amount(String unpaid_amount) {
            this.unpaid_amount = unpaid_amount;
        }

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

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public ArrayList<Course> getCourses() {
            return courses;
        }

        public void setCourses(ArrayList<Course> courses) {
            this.courses = courses;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getParentname() {
            return parentname;
        }

        public void setParentname(String parentname) {
            this.parentname = parentname;
        }

        public String getParentmobileno() {
            return parentmobileno;
        }

        public void setParentmobileno(String parentmobileno) {
            this.parentmobileno = parentmobileno;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getJoiningDate() {
            return joiningDate;
        }

        public void setJoiningDate(String joiningDate) {
            this.joiningDate = joiningDate;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }
    }

    public class Course {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("fees")
        @Expose
        private String fees;
        @SerializedName("duration")
        @Expose
        private String duration;
        @SerializedName("course_status")
        @Expose
        private String courseStatus;
        @SerializedName("book_material")
        @Expose
        private String bookMaterial;
        @SerializedName("bag")
        @Expose
        private String bag;
        @SerializedName("certificate")
        @Expose
        private String certificate;

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

        public String getFees() {
            return fees;
        }

        public void setFees(String fees) {
            this.fees = fees;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getCourseStatus() {
            return courseStatus;
        }

        public void setCourseStatus(String courseStatus) {
            this.courseStatus = courseStatus;
        }

        public String getBookMaterial() {
            return bookMaterial;
        }

        public void setBookMaterial(String bookMaterial) {
            this.bookMaterial = bookMaterial;
        }

        public String getBag() {
            return bag;
        }

        public void setBag(String bag) {
            this.bag = bag;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

    }
}
