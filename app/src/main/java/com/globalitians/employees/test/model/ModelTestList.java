package com.globalitians.employees.test.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelTestList {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("tests")
    @Expose
    private ArrayList<Test> tests = null;

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

    public ArrayList<Test> getTests() {
        return tests;
    }

    public void setTests(ArrayList<Test> tests) {
        this.tests = tests;
    }
    public class Test {
        @SerializedName("is_marks_added")
        @Expose
        private boolean is_marks_added=false;

        public boolean isMarks_added() {
            return is_marks_added;
        }

        public void setMarks_added(boolean marks_added) {
            this.is_marks_added = marks_added;
        }

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("totalmarks")
        @Expose
        private String totalmarks;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("time")
        @Expose
        private Object time;
        @SerializedName("exam_status")
        @Expose
        private String examStatus;

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

        public String getTotalmarks() {
            return totalmarks;
        }

        public void setTotalmarks(String totalmarks) {
            this.totalmarks = totalmarks;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Object getTime() {
            return time;
        }

        public void setTime(Object time) {
            this.time = time;
        }

        public String getExamStatus() {
            return examStatus;
        }

        public void setExamStatus(String examStatus) {
            this.examStatus = examStatus;
        }

    }
}
