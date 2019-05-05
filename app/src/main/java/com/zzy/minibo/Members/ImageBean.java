package com.zzy.minibo.Members;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageBean implements Parcelable{

    private String path;
    private int size;
    private String displayname;

    private boolean isSelected;

    public ImageBean(String path, int size, String displayname){
        this.path = path;
        this.size = size;
        this.displayname = displayname;
    }


    protected ImageBean(Parcel in) {
        path = in.readString();
        size = in.readInt();
        displayname = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(size);
        dest.writeString(displayname);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
