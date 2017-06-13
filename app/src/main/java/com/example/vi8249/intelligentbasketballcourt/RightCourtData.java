package com.example.vi8249.intelligentbasketballcourt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vi8249 on 2017/6/12.
 */

public class RightCourtData implements Parcelable {
    public static final Parcelable.Creator<RightCourtData> CREATOR = new Parcelable.Creator<RightCourtData>() {
        public RightCourtData createFromParcel(Parcel in) {
            return new RightCourtData(in);
        }

        public RightCourtData[] newArray(int size) {
            return new RightCourtData[size];

        }
    };
    String timestamp;
    float data;

    protected RightCourtData(Parcel in) {
        timestamp = in.readString();
        data = in.readFloat();
    }

    public RightCourtData() {

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
