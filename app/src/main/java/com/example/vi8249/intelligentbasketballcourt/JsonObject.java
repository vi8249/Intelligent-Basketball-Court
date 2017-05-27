package com.example.vi8249.intelligentbasketballcourt;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vi8249 on 2017/5/24.
 */

public class JsonObject {
    @SerializedName("message")
    private String message;

    @SerializedName("deviceId")
    private String deviceID;

    @SerializedName("dataChannels")
    private List<DataChannels> dataChannels;

    public List<DataChannels> getDataChannels() { return dataChannels; }

    class DataChannels {
        @SerializedName("dataChnId")
        private String dataChnID;

        @SerializedName("dataPoints")
        private List<DataPoints> dataPoints;

        public String getDataChnID() { return dataChnID; }

        public List<DataPoints> getDataPoints() { return dataPoints; }

        class DataPoints {
            @SerializedName("values")
            private Values values;

            public Values getValues() { return values; }

            class Values {
                @SerializedName("value")
                private float value;

                public float getValue() { return value; }
            }
        }
    }
}