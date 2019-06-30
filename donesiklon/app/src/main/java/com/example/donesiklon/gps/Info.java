package com.example.donesiklon.gps;

import android.os.Parcel;
import android.os.Parcelable;

public class Info implements Parcelable {
    public String distance;
    public String duration;
    public String restaurantId;

    public Info(){}

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public Info(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.restaurantId = data[0];
        this.distance = data[1];
        this.duration = data[2];
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    @Override
    public String toString() {
        return "Info{" +
                "distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                '}';
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {
                this.restaurantId,
                this.distance,
                this.duration});
    }
}
