package com.globalitians.employees.test.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestStudentsModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("test_detail")
    @Expose
    private TestDetail testDetail;

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

    public TestDetail getTestDetail() {
        return testDetail;
    }

    public void setTestDetail(TestDetail testDetail) {
        this.testDetail = testDetail;
    }


    public class TestDetail {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("testname")
        @Expose
        private String testname;
        @SerializedName("batch_name")
        @Expose
        private String batch_name;
        @SerializedName("totalmarks")
        @Expose
        private String totalmarks;
        @SerializedName("passing_marks")
        @Expose
        private String passingMarks;
        @SerializedName("time")
        @Expose
        private String time;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("exam_status")
        @Expose
        private String examStatus;
        @SerializedName("test_students")
        @Expose
        private ArrayList<TestStudent> testStudents = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTestname() {
            return testname;
        }

        public String getBatch_name() {
            return batch_name;
        }

        public void setBatch_name(String batch_name) {
            this.batch_name = batch_name;
        }

        public void setTestname(String testname) {
            this.testname = testname;
        }

        public String getTotalmarks() {
            return totalmarks;
        }

        public void setTotalmarks(String totalmarks) {
            this.totalmarks = totalmarks;
        }

        public String getPassingMarks() {
            return passingMarks;
        }

        public void setPassingMarks(String passingMarks) {
            this.passingMarks = passingMarks;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getExamStatus() {
            return examStatus;
        }

        public void setExamStatus(String examStatus) {
            this.examStatus = examStatus;
        }

        public ArrayList<TestStudent> getTestStudents() {
            return testStudents;
        }

        public void setTestStudents(ArrayList<TestStudent> testStudents) {
            this.testStudents = testStudents;
        }

        public class TestStudent {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("fname")
            @Expose
            private String fname;
            private String obtained_marks="00";
            private String status_present_absent="P";
            @SerializedName("lname")
            @Expose
            private String lname;
            @SerializedName("test_id")
            @Expose
            private Integer testId;

            public String getStatus_present_absent() {
                return status_present_absent;
            }

            public void setStatus_present_absent(String status_present_absent) {
                this.status_present_absent = status_present_absent;
            }

            public String getObtained_marks() {
                return obtained_marks;
            }

            public void setObtained_marks(String obtained_marks) {
                this.obtained_marks = obtained_marks;
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

            public Integer getTestId() {
                return testId;
            }

            public void setTestId(Integer testId) {
                this.testId = testId;
            }
        }
    }
}
