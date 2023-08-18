package com.globalitians.employees.batches.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BatchDetailsModel implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("batch_detail")
    @Expose
    private BatchDetail batchDetail;

    protected BatchDetailsModel(Parcel in) {
        status = in.readString();
        message = in.readString();
        batchDetail = in.readParcelable(BatchDetail.class.getClassLoader());
    }

    public static final Creator<BatchDetailsModel> CREATOR = new Creator<BatchDetailsModel>() {
        @Override
        public BatchDetailsModel createFromParcel(Parcel in) {
            return new BatchDetailsModel(in);
        }

        @Override
        public BatchDetailsModel[] newArray(int size) {
            return new BatchDetailsModel[size];
        }
    };

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

    public BatchDetail getBatchDetail() {
        return batchDetail;
    }

    public void setBatchDetail(BatchDetail batchDetail) {
        this.batchDetail = batchDetail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
        parcel.writeString(message);
        parcel.writeParcelable(batchDetail,i);
    }

    public static class BatchDetail implements Parcelable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("alias")
        @Expose
        private String alias;
        @SerializedName("branch_id")
        @Expose
        private Integer branchId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("branch_name")
        @Expose
        private String branchName;
        @SerializedName("faculties")
        @Expose
        private ArrayList<Faculty> faculties = null;
        @SerializedName("students")
        @Expose
        private ArrayList<Student> students = null;
        @SerializedName("edited_by")
        @Expose
        private Integer editedBy;
        @SerializedName("edited_user_name")
        @Expose
        private String editedUserName;

        protected BatchDetail(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readInt();
            }
            name = in.readString();
            alias = in.readString();
            if (in.readByte() == 0) {
                branchId = null;
            } else {
                branchId = in.readInt();
            }
            createdAt = in.readString();
            branchName = in.readString();
            if (in.readByte() == 0) {
                editedBy = null;
            } else {
                editedBy = in.readInt();
            }
            editedUserName = in.readString();
        }

        public static final Creator<BatchDetail> CREATOR = new Creator<BatchDetail>() {
            @Override
            public BatchDetail createFromParcel(Parcel in) {
                return new BatchDetail(in);
            }

            @Override
            public BatchDetail[] newArray(int size) {
                return new BatchDetail[size];
            }
        };

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

        public Integer getBranchId() {
            return branchId;
        }

        public void setBranchId(Integer branchId) {
            this.branchId = branchId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public ArrayList<Faculty> getFaculties() {
            return faculties;
        }

        public void setFaculties(ArrayList<Faculty> faculties) {
            this.faculties = faculties;
        }

        public ArrayList<Student> getStudents() {
            return students;
        }

        public void setStudents(ArrayList<Student> students) {
            this.students = students;
        }

        public Integer getEditedBy() {
            return editedBy;
        }

        public void setEditedBy(Integer editedBy) {
            this.editedBy = editedBy;
        }

        public String getEditedUserName() {
            return editedUserName;
        }

        public void setEditedUserName(String editedUserName) {
            this.editedUserName = editedUserName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (id == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(id);
            }
            parcel.writeString(name);
            parcel.writeString(alias);
            if (branchId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(branchId);
            }
            parcel.writeString(createdAt);
            parcel.writeString(branchName);
            if (editedBy == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(editedBy);
            }
            parcel.writeString(editedUserName);
        }

        public static class Faculty implements Parcelable{

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("image")
            @Expose
            private String image;

            protected Faculty(Parcel in) {
                if (in.readByte() == 0) {
                    id = null;
                } else {
                    id = in.readInt();
                }
                name = in.readString();
                image = in.readString();
            }

            public static final Creator<Faculty> CREATOR = new Creator<Faculty>() {
                @Override
                public Faculty createFromParcel(Parcel in) {
                    return new Faculty(in);
                }

                @Override
                public Faculty[] newArray(int size) {
                    return new Faculty[size];
                }
            };

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

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                if (id == null) {
                    parcel.writeByte((byte) 0);
                } else {
                    parcel.writeByte((byte) 1);
                    parcel.writeInt(id);
                }
                parcel.writeString(name);
                parcel.writeString(image);
            }
        }

        public static class Student implements Parcelable{

            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("first_name")
            @Expose
            private String firstName;
            @SerializedName("last_name")
            @Expose
            private String lastName;
            @SerializedName("image")
            @Expose
            private String image;

            public boolean isSelected = false;

            public Student() {

            }

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                this.isSelected = selected;
            }

            public Student(Parcel in) {
                if (in.readByte() == 0) {
                    id = null;
                } else {
                    id = in.readInt();
                }
                firstName = in.readString();
                lastName = in.readString();
                image = in.readString();
            }

            public static final Creator<Student> CREATOR = new Creator<Student>() {
                @Override
                public Student createFromParcel(Parcel in) {
                    return new Student(in);
                }

                @Override
                public Student[] newArray(int size) {
                    return new Student[size];
                }
            };

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                if (id == null) {
                    parcel.writeByte((byte) 0);
                } else {
                    parcel.writeByte((byte) 1);
                    parcel.writeInt(id);
                }
                parcel.writeString(firstName);
                parcel.writeString(lastName);
                parcel.writeString(image);
            }
        }

    }

}
