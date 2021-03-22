package com.project.simplecreative.Model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("display_name")
    @Expose
    private String display_name;
    @SerializedName("profile_photo")
    @Expose
    private String profile_photo;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("phone_number")
    @Expose
    private long phone_number;

    public UserModel(String user_id, String email, String name, String display_name, String profile_photo, String website, String description, long phone_number) {
        this.user_id = user_id;
        this.email = email;
        this.name = name;
        this.display_name = display_name;
        this.profile_photo = profile_photo;
        this.website = website;
        this.description = description;
        this.phone_number = phone_number;
    }

    public UserModel(){ }

    protected UserModel(Parcel in) {
        user_id = in.readString();
        email = in.readString();
        name = in.readString();
        display_name = in.readString();
        profile_photo = in.readString();
        website = in.readString();
        description = in.readString();
        phone_number = in.readLong();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", display_name='" + display_name + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", website='" + website + '\'' +
                ", description='" + description + '\'' +
                ", phone_number=" + phone_number +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(display_name);
        dest.writeString(profile_photo);
        dest.writeString(website);
        dest.writeString(description);
        dest.writeLong(phone_number);
    }
}
