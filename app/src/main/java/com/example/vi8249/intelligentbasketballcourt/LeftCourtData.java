package com.example.vi8249.intelligentbasketballcourt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vi8249 on 2017/6/12.
 */

public class TemperatureData implements Parcelable {
    public static final Parcelable.Creator<TemperatureData> CREATOR = new Parcelable.Creator<TemperatureData>() {
        public TemperatureData createFromParcel(Parcel in) {
            return new TemperatureData(in);
        }

        public TemperatureData[] newArray(int size) {
            return new TemperatureData[size];

        }
    };
    String timestamp;
    float data;

    protected TemperatureData(Parcel in) {
        timestamp = in.readString();
        data = in.readFloat();
    }

    public TemperatureData() {

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
