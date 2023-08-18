package com.globalitians.employees.test.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestDetailsModel {
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
        @SerializedName("branch_name")
        @Expose
        private String branchName;
        @SerializedName("batch_name")
        @Expose
        private String batchName;
        @SerializedName("edited_by")
        @Expose
        private Integer editedBy;
        @SerializedName("edited_user_name")
        @Expose
        private String editedUserName;
        @SerializedName("students_marks")
        @Expose
        private ArrayList<StudentsMark> studentsMarks = null;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTestname() {
            return testname;
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

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getBatchName() {
            return batchName;
        }

        public void setBatchName(String batchName) {
            this.batchName = batchName;
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

        public ArrayList<StudentsMark> getStudentsMarks() {
            return studentsMarks;
        }

        public void setStudentsMarks(ArrayList<StudentsMark> studentsMarks) {
            this.studentsMarks = studentsMarks;
        }

        public class StudentsMark {

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("student_id")
            @Expose
            private Integer studentId;
            @SerializedName("student_fname")
            @Expose
            private String studentFname;
            @SerializedName("student_lname")
            @Expose
            private String studentLname;
            @SerializedName("obtainedmarks")
            @Expose
            private String obtainedmarks;
            @SerializedName("attendence")
            @Expose
            private String attendence;

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getStudentId() {
                return studentId;
            }

            public void setStudentId(Integer studentId) {
                this.studentId = studentId;
            }

            public String getStudentFname() {
                return studentFname;
            }

            public void setStudentFname(String studentFname) {
                this.studentFname = studentFname;
            }

            public String getStudentLname() {
                return studentLname;
            }

            public void setStudentLname(String studentLname) {
                this.studentLname = studentLname;
            }

            public String getObtainedmarks() {
                return obtainedmarks;
            }

            public void setObtainedmarks(String obtainedmarks) {
                this.obtainedmarks = obtainedmarks;
            }

            public String getAttendence() {
                return attendence;
            }

            public void setAttendence(String attendence) {
                this.attendence = attendence;
            }

        }

    }
}
