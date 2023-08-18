package com.globalitians.employees.courses.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelCourseCategoriesList {
    @SerializedName("status")
    @Expose
    private String status = "";
    @SerializedName("message")
    @Expose
    private String message = "";

    @SerializedName("categories")
    @Expose
    public ArrayList<Categories> alCategories;

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

    public ArrayList<Categories> getAlCategories() {
        return alCategories;
    }

    public void setAlCategories(ArrayList<Categories> alCategories) {
        this.alCategories = alCategories;
    }

    public class Categories {
        @SerializedName("id")
        @Expose
        public String id = "";

        @SerializedName("name")
        @Expose
        public String name = "";

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
