package com.globalitians.employees.courses.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModelCourseDurationList {
    @SerializedName("durations")
    @Expose
    public ArrayList<Duration> alDuration;

    public ArrayList<Duration> getAlDuration() {
        return alDuration;
    }

    public void setAlDuration(ArrayList<Duration> alDuration) {
        this.alDuration = alDuration;
    }

    public class Duration {
        @SerializedName("duration")
        @Expose
        public String duration = "";

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
}
