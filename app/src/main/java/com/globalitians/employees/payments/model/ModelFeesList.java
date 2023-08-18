package com.globalitians.employees.payments.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelFeesList {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("student_detail")
    @Expose
    private ArrayList<StudentDetail> studentDetail = null;
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

    public ArrayList<StudentDetail> getStudentDetail() {
        return studentDetail;
    }

    public void setStudentDetail(ArrayList<StudentDetail> studentDetail) {
        this.studentDetail = studentDetail;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }
    public class Payment {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("payment")
        @Expose
        private String payment;
        @SerializedName("payment_date")
        @Expose
        private String paymentDate;
        @SerializedName("payment_type")
        @Expose
        private String paymentType;
        @SerializedName("check_no")
        @Expose
        private String checkNo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public String getCheckNo() {
            return checkNo;
        }

        public void setCheckNo(String checkNo) {
            this.checkNo = checkNo;
        }
    }

    public class StudentDetail {

        @SerializedName("student_id")
        @Expose
        private Integer studentId;
        @SerializedName("student_name")
        @Expose
        private String studentName;
        @SerializedName("mobileno")
        @Expose
        private String mobileno;
        @SerializedName("total_amount")
        @Expose
        private String totalAmount;
        @SerializedName("paid_amounnt")
        @Expose
        private Integer paidAmounnt;
        @SerializedName("unpaid_amount")
        @Expose
        private String unpaidAmount;
        @SerializedName("student_image")
        @Expose
        private String student_image;

        public String getStudent_image() {
            return student_image;
        }

        public void setStudent_image(String student_image) {
            this.student_image = student_image;
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

        public String getMobileno() {
            return mobileno;
        }

        public void setMobileno(String mobileno) {
            this.mobileno = mobileno;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getPaidAmounnt() {
            return paidAmounnt;
        }

        public void setPaidAmounnt(Integer paidAmounnt) {
            this.paidAmounnt = paidAmounnt;
        }

        public String getUnpaidAmount() {
            return unpaidAmount;
        }

        public void setUnpaidAmount(String unpaidAmount) {
            this.unpaidAmount = unpaidAmount;
        }
    }
}
