package com.globalitians.employees.multiple_attendance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MultipleDatewisestudentsattendancemodel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("student_attendences")
    @Expose
    private ArrayList<StudentAttendence> studentAttendences = null;

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

    public ArrayList<StudentAttendence> getStudentAttendences() {
        return studentAttendences;
    }

    public void setStudentAttendences(ArrayList<StudentAttendence> studentAttendences) {
        this.studentAttendences = studentAttendences;
    }

    public class StudentAttendence {

        @SerializedName("student_id")
        @Expose
        private Integer studentId;
        @SerializedName("student_name")
        @Expose
        private String studentName;
        @SerializedName("student_image")
        @Expose
        private String studentImage;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("in")
        @Expose
        private String in;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("batch_id")
        @Expose
        private Integer batchId;
        @SerializedName("batch_name")
        @Expose
        private String batchName;

        public Integer getStudentId() {
            return studentId;
        }

        public void setStudentId(Integer studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentImage() {
            return studentImage;
        }

        public void setStudentImage(String studentImage) {
            this.studentImage = studentImage;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getIn() {
            return in;
        }

        public void setIn(String in) {
            this.in = in;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

    }

}
