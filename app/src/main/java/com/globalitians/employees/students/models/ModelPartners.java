package com.globalitians.employees.students.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelPartners {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("partners")
    @Expose
    private ArrayList<Partner> partners = null;

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

    public ArrayList<Partner> getPartners() {
        return partners;
    }

    public void setPartners(ArrayList<Partner> partners) {
        this.partners = partners;
    }

    public class Partner {

        @SerializedName("0")
        @Expose
        private String _0;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("partner_userid")
        @Expose
        private String partnerUserid;
        @SerializedName("image")
        @Expose
        private String image;

        public String get0() {
            return _0;
        }

        public void set0(String _0) {
            this._0 = _0;
        }

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

        public String getPartnerUserid() {
            return partnerUserid;
        }

        public void setPartnerUserid(String partnerUserid) {
            this.partnerUserid = partnerUserid;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }

}
