package com.globalitians.employees.payments.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelAllFeesPayments {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payments")
    @Expose
    private ArrayList<Payment> payments = null;

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

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }

    public class Payment {

        @SerializedName("payment_id")
        @Expose
        private Integer paymentId;
        @SerializedName("student_id")
        @Expose
        private Integer studentId;
        @SerializedName("student_name")
        @Expose
        private String studentName;
        @SerializedName("student_image")
        @Expose
        private String studentImage;
        @SerializedName("payment_date")
        @Expose
        private String paymentDate;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("payment_type")
        @Expose
        private String payment_type;
        @SerializedName("check_no")
        @Expose
        private String check_no;

        public String getPayment_type() {
            return payment_type;
        }

        public void setPayment_type(String payment_type) {
            this.payment_type = payment_type;
        }

        public String getCheck_no() {
            return check_no;
        }

        public void setCheck_no(String check_no) {
            this.check_no = check_no;
        }

        public Integer getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(Integer paymentId) {
            this.paymentId = paymentId;
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

        public String getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

    }
}
