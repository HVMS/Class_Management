package com.globalitians.employees.attendence.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelAttendanceList {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("attendences")
    @Expose
    private ArrayList<Attendence> attendences = null;

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

    public ArrayList<Attendence> getAttendences() {
        return attendences;
    }

    public void setAttendences(ArrayList<Attendence> attendences) {
        this.attendences = attendences;
    }

    public class Attendence {

        @SerializedName("attendence_id")
        @Expose
        private Integer attendence_id;
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
        @SerializedName("in_time")
        @Expose
        private String in_time;
        @SerializedName("out_time")
        @Expose
        private String out_time;
        @SerializedName("status")
        @Expose
        private String status;

        public Integer getAttendence_id() {
            return attendence_id;
        }

        public void setAttendence_id(Integer attendence_id) {
            this.attendence_id = attendence_id;
        }

        public String getIn_time() {
            return in_time;
        }

        public void setIn_time(String in_time) {
            this.in_time = in_time;
        }

        public String getOut_time() {
            return out_time;
        }

        public void setOut_time(String out_time) {
            this.out_time = out_time;
        }

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

}