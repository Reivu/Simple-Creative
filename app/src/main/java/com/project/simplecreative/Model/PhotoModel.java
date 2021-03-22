package com.project.simplecreative.Model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("Duplicates")
public class PhotoModel implements Parcelable {

    @SerializedName("title")
    private String title;
    @SerializedName("caption")
    private String caption;
    @SerializedName("date")
    private String date;
    @SerializedName("path")
    private String path;
    @SerializedName("photo_id")
    private String photo_id;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("tags")
    private String tags;

    public PhotoModel(String title, String caption, String date, String path, String photo_id, String user_id, String tags) {
        this.title = title;
        this.caption = caption;
        this.date = date;
        this.path = path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
    }

    public PhotoModel() {
    }

    protected PhotoModel(Parcel in) {
        title = in.readString();
        caption = in.readString();
        date = in.readString();
        path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
    }

    public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel in) {
            return new PhotoModel(in);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                ", date='" + date + '\'' +
                ", path='" + path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(caption);
        dest.writeString(date);
        dest.writeString(path);
        dest.writeString(photo_id);
        dest.writeString(user_id);
        dest.writeString(tags);
    }
}
