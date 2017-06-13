package com.example.vi8249.intelligentbasketballcourt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vi8249 on 2017/6/13.
 */

public class HumidityData implements Parcelable {
    public static final Parcelable.Creator<HumidityData> CREATOR = new Parcelable.Creator<HumidityData>() {
        public HumidityData createFromParcel(Parcel in) {
            return new HumidityData(in);
        }

        public HumidityData[] newArray(int size) {
            return new HumidityData[size];

        }
    };
    String timestamp;
    float data;

    protected HumidityData(Parcel in) {
        timestamp = in.readString();
        data = in.readFloat();
    }

    public HumidityData() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timestamp);
        dest.writeFloat(data);
    }
}
