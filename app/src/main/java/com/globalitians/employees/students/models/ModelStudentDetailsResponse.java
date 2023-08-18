package com.globalitians.employees.students.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelStudentDetailsResponse implements Serializable {

    /*
    * {"status":"success",
    * "message":"Student Detail Found",
    * "student_detail":
    * {"id":24,"unique_id":"ST-1024","fname":"Jaimin","lname":"Modi","slug":"H7mvEwiC","parentname":"Dinesh","mobileno":"9999999956","parentmobileno":"8888998877","address":"Amarjyot","email":"jaimin@gmail.com","joining_date":"10-06-2018","dob":"07-05-1996","reference":"modi","courses":[{"id":11,"name":"CCC","image":"http:\/\/globalitians.com\/application\/pic\/S8Gj3aZ8.png"}],"branch":{"id":1,"name":"Vastral"},"image":"http:\/\/globalitians.com\/application\/student\/0kmZyaNK.jpg"}}
    * */
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("student_detail")
    @Expose
    private StudentDetail studentDetail;

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

    public StudentDetail getStudentDetail() {
        return studentDetail;
    }

    public void setStudentDetail(StudentDetail studentDetail) {
        this.studentDetail = studentDetail;
    }

    public class StudentDetail {
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("unique_id")
        @Expose
        private String uniqueId;
        @SerializedName("fname")
        @Expose
        private String fname;
        @SerializedName("lname")
        @Expose
        private String lname;
        @SerializedName("slug")
        @Expose
        private String slug;
        @SerializedName("parentname")
        @Expose
        private String parentname;
        @SerializedName("mobileno")
        @Expose
        private String mobileno;
        @SerializedName("parentmobileno")
        @Expose
        private String parentmobileno;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("joining_date")
        @Expose
        private String joiningDate;
        @SerializedName("installment_date")
        @Expose
        private String installmentDate;
        @SerializedName("dob")
        @Expose
        private String dob;
        @SerializedName("reference")
        @Expose
        private String reference;
        @SerializedName("in_out_status")
        @Expose
        private String in_out_status;
        @SerializedName("unpaid_amount")
        @Expose
        private String unpaid_amount;

        public String getUnpaid_amount() {
            return unpaid_amount;
        }

        public void setUnpaid_amount(String unpaid_amount) {
            this.unpaid_amount = unpaid_amount;
        }

        public String getIn_out_status() {
            return in_out_status;
        }

        public void setIn_out_status(String in_out_status) {
            this.in_out_status = in_out_status;
        }

        @SerializedName("courses")
        @Expose
        private List<Course> courses = null;
        @SerializedName("branch")
        @Expose
        private Branch branch;
        @SerializedName("image")
        @Expose
        private String image;

        public String getInstallmentDate() {
            return installmentDate;
        }

        public void setInstallmentDate(String installmentDate) {
            this.installmentDate = installmentDate;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
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

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getParentname() {
            return parentname;
        }

        public void setParentname(String parentname) {
            this.parentname = parentname;
        }

        public String getMobileno() {
            return mobileno;
        }

        public void setMobileno(String mobileno) {
            this.mobileno = mobileno;
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

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }

        public Branch getBranch() {
            return branch;
        }

        public void setBranch(Branch branch) {
            this.branch = branch;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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
            @SerializedName("image")
            @Expose
            private String image;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

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

        public class Branch {

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

    }
}
