package com.globalitians.employees.multiple_attendance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MultipleMonthWiseAttendanceReportModelClass {

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
        @SerializedName("total_present")
        @Expose
        private Integer totalPresent;
        @SerializedName("total_absent")
        @Expose
        private Integer totalAbsent;

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

        public Integer getTotalPresent() {
            return totalPresent;
        }

        public void setTotalPresent(Integer totalPresent) {
            this.totalPresent = totalPresent;
        }

        public Integer getTotalAbsent() {
            return totalAbsent;
        }

        public void setTotalAbsent(Integer totalAbsent) {
            this.totalAbsent = totalAbsent;
        }

    }

}
