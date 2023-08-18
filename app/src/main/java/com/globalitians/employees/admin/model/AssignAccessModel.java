package com.globalitians.employees.admin.model;

import com.globalitians.employees.utility.CommonUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AssignAccessModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("access_list")
    @Expose
    private ArrayList<AccessList> accessList = new ArrayList<>();


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

    public ArrayList<AccessList> getAccessList() {
        return accessList;
    }

    public void setAccessList(ArrayList<AccessList> accessList) {
        this.accessList = accessList;
    }

    public class AccessList {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("alias")
        @Expose
        private String alias;
        @SerializedName("assign_for")
        @Expose
        private String assign_for;

        private ArrayList<AccessItems> accessItems = new ArrayList<>();

        public ArrayList<AccessItems> getAccessItems() {
            return accessItems;
        }

        public void setAccessItems(ArrayList<AccessItems> accessItems) {
            this.accessItems = accessItems;
        }

        public String getAssign_for() {
            return assign_for;
        }

        public void setAssign_for(String assign_for) {
            this.assign_for = assign_for;
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

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

    }
}
