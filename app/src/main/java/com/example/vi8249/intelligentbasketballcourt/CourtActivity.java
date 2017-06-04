package com.example.vi8249.intelligentbasketballcourt;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private static String[] url = {"https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Temp_Display/datapoints",
                                    "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Hum_Display/datapoints",
                                    "https://api.mediatek.com/mcs/v2/devices/DRbB5aVM/datachannels/Toggle_Button/datapoints"};
    HttpURLConnection connection = null;
    String responseString = "";
    private ProgressDialog pDialog;
    private TextView temperature, humidity;
    private ImageView leftCourt, rightCourt;
    private Button mBtn;
    private View rootView;

    private static final AtomicInteger PROGRESS_NUM = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.court_view, container, false);
        temperature = (TextView) rootView.findViewById(R.id.temperature);
        humidity = (TextView) rootView.findViewById(R.id.humidity);
        leftCourt = (ImageView) rootView.findViewById(R.id.leftCourt);
        rightCourt = (ImageView) rootView.findViewById(R.id.rightCourt);

        mBtn = (Button) rootView.findViewById(R.id.button);
        mBtn.setOnClickListener(mBtnOnClick);

        for (String anUrl : url) new LoadingMCSAsyncTask().execute(anUrl);

        return rootView;
    }

    private View.OnClickListener mBtnOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // refresh
        }
    };

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
                        Log.d("json", tempStr);
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
            if(x == 0) {
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
            if(x == 0 && pDialog.isShowing())
                pDialog.dismiss();

            if (invalidUrl) {
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("Connection failed")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
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
                    CourtActivity.this.temperature.setText(temperature);
                    break;
                case "Hum_Display":
                    String humidity = Float.toString(tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue());
                    CourtActivity.this.humidity.setText(humidity);
                    break;
                case "Toggle_Button":
                    if ((int) tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue() == 0)
                        leftCourt.setImageResource(R.drawable.left_court);
                    else
                        leftCourt.setImageResource(R.drawable.left_court_dark);
                    Log.d("json", Integer.toString((int)tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue()));
                    break;
            }
        }
    }
}
