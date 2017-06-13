package com.example.vi8249.intelligentbasketballcourt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vi8249 on 2017/6/12.
 */

public class LeftCourtData implements Parcelable {
    public static final Parcelable.Creator<LeftCourtData> CREATOR = new Parcelable.Creator<LeftCourtData>() {
        public LeftCourtData createFromParcel(Parcel in) {
            return new LeftCourtData(in);
        }

        public LeftCourtData[] newArray(int size) {
            return new LeftCourtData[size];

        }
    };
    String timestamp;
    float data;

    protected LeftCourtData(Parcel in) {
        timestamp = in.readString();
        data = in.readFloat();
    }

    public LeftCourtData() {

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
