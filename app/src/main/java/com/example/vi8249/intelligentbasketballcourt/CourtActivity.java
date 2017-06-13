package com.example.vi8249.intelligentbasketballcourt;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.DialogInterface;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vi8249 on 2017/5/27.
 */

public class CourtActivity extends Fragment {
    private static final AtomicInteger PROGRESS_NUM = new AtomicInteger(0);
    private static String[] url = {
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Temp_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Hum_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Vib_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DIK4dY0L/datachannels/Vib2_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Bat_Display/datapoints"
    };
    HttpURLConnection connection = null;
    String responseString = "";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog pDialog;
    private TextView temperature, humidity, battery;
    private ImageView leftCourt, rightCourt;
    private View rootView;

    private String IntentTemperature, IntentHumidity, IntentBattery;
    private boolean IntentLeftCourt, IntentRightCourt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.court_view, container, false);

        temperature = (TextView) rootView.findViewById(R.id.temperature);
        humidity = (TextView) rootView.findViewById(R.id.humidity);
        leftCourt = (ImageView) rootView.findViewById(R.id.leftCourt);
        rightCourt = (ImageView) rootView.findViewById(R.id.rightCourt);
        battery = (TextView) rootView.findViewById(R.id.battery);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
                for (String anUrl : url) new LoadingMCSAsyncTask().execute(anUrl);
            }
        });

        if (!IntentTemperature.equals("N/A"))
            temperature.setText(IntentTemperature + " °C");
        else
            temperature.setText(IntentTemperature);
        if (!IntentHumidity.equals("N/A"))
            humidity.setText(IntentHumidity + " %");
        else
            humidity.setText(IntentHumidity);
        if (!IntentBattery.equals("N/A")) {
            battery.setText(IntentBattery + " %");
            if(IntentBattery.equals("33") || IntentBattery.equals("0")) {
                CourtActivity.this.battery.setTextColor(Color.RED);
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("低電量提示")
                        .setMessage("板子要沒電了QQ\n快去換電池！ ( ･᷄ὢ･᷅ )")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
            else{
                CourtActivity.this.battery.setTextColor(CourtActivity.this.temperature.getTextColors());
            }
        }
        else
            battery.setText(IntentBattery);

        if (IntentLeftCourt)
            leftCourt.setImageResource(R.drawable.left_court);
        else
            leftCourt.setImageResource(R.drawable.left_court_dark);
        if (IntentRightCourt)
            rightCourt.setImageResource(R.drawable.right_court);
        else
            rightCourt.setImageResource(R.drawable.right_court_dark);

        return rootView;
    }

    public void Initialize(String intentTemperature, String intentHumidity, boolean leftCourt, boolean rightCourt, String intentBattery) {
        IntentTemperature = intentTemperature;
        IntentHumidity = intentHumidity;
        IntentLeftCourt = leftCourt;
        IntentRightCourt = rightCourt;
        IntentBattery = intentBattery;
    }

    private class LoadingMCSAsyncTask extends AsyncTask<String, Integer, Integer> {
        private boolean invalidUrl = false;

        @Override
        protected Integer doInBackground(String... param) {
            try {
                URL url = new URL(param[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(2000);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String tempStr;
                    StringBuffer stringBuffer = new StringBuffer();

                    while ((tempStr = bufferedReader.readLine()) != null) {
                        stringBuffer.append(tempStr);
                        //Log.d("json", tempStr);
                    }

                    bufferedReader.close();
                    inputStream.close();

                    responseString = stringBuffer.toString();
                } else {
                    invalidUrl = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //if(connection != null)
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            int x = PROGRESS_NUM.getAndIncrement();
            if (x == 0) {
                pDialog = new ProgressDialog(getActivity());
                pDialog = ProgressDialog.show(getActivity(), "Message", "Loading...", true);
                pDialog.setCancelable(false);
            }
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            int x = PROGRESS_NUM.decrementAndGet();
            if (x == 0 && pDialog.isShowing())
                pDialog.dismiss();

            if (invalidUrl) {
                CourtActivity.this.temperature.setText("N/A");
                CourtActivity.this.humidity.setText("N/A");
                CourtActivity.this.battery.setText("N/A");
                /*new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("Connection failed")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();*/
            } else {
                Gson gson = new Gson();
                JsonObject tJsonObject = gson.fromJson(responseString, JsonObject.class);
                chooseViewToUpdate(tJsonObject);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        private void chooseViewToUpdate(JsonObject tJsonObject) {
            switch (tJsonObject.getDataChannels().get(0).getDataChnID()) {
                case "Temp_Display":
                    String temperature = Float.toString(tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue());
                    CourtActivity.this.temperature.setText(temperature + " °C");
                    break;
                case "Hum_Display":
                    String humidity = Float.toString(tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue());
                    CourtActivity.this.humidity.setText(humidity + " %");
                    break;
                case "Vib_Display":
                    if ((int) tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue() == 0)
                        leftCourt.setImageResource(R.drawable.left_court);
                    else
                        leftCourt.setImageResource(R.drawable.left_court_dark);
                    //Log.d("json", Integer.toString((int)tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue()));
                    break;
                case "Vib2_Display":
                    if ((int) tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue() == 0)
                        rightCourt.setImageResource(R.drawable.right_court);
                    else
                        rightCourt.setImageResource(R.drawable.right_court_dark);
                    //Log.d("json", Integer.toString((int)tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue()));
                    break;
                case "Bat_Display":
                    String battery = Integer.toString((int)tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue());
                    CourtActivity.this.battery.setText(battery + " %");
                    if(battery.equals("33") || battery.equals("0")) {
                        CourtActivity.this.battery.setTextColor(Color.RED);
                        new android.app.AlertDialog.Builder(getActivity())
                                .setTitle("低電量提示")
                                .setMessage("板子要沒電了QQ\n快去換電池！ ( ･᷄ὢ･᷅ )")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                    else{
                        CourtActivity.this.battery.setTextColor(CourtActivity.this.temperature.getTextColors());
                    }
                    break;
            }
        }
    }
}
