package com.rhewumapp.Activity.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemData implements Parcelable {
    private String text;

    public ItemData(String text, int imageResId) {
        this.text = text;
        this.imageResId = imageResId;
    }

    protected ItemData(Parcel in) {
        text = in.readString();
        imageResId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(imageResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemData> CREATOR = new Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel in) {
            return new ItemData(in);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    private int imageResId;
}
