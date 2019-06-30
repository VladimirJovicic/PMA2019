package com.example.donesiklon.gps;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class InfoList implements Parcelable {
    private List<Info> list;

    // get an object List <ApplicationInfo> constructor
    public InfoList(List<Info> l) {
        this.list = l;
    }
    public InfoList(Parcel in) {
        in.readTypedList(list, Info.CREATOR); // read
    }
    public List<Info> getList(){
        return list;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { // write
        dest.writeTypedList(list);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Info> CREATOR = new Parcelable.Creator<Info>() {
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
}
