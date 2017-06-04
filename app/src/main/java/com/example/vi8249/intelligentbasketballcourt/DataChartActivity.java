package com.example.vi8249.intelligentbasketballcourt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.example.vi8249.intelligentbasketballcourt.R.attr.colorPrimary;

/**
 * Created by vi8249 on 2017/6/3.
 */

public class DataChartActivity extends Fragment {
    private static String[] url = {
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Temp_Display/datapoints.csv?start=1496409690000&end=1496411743000&limit=100",
    };

    HttpURLConnection connection = null;
    private ProgressDialog pDialog;
    private Button mBtn;
    private View rootView;

    private String startTime, endTime;
    private Context mContext;
    private ArrayList<TemperatureData> tDatalist = new ArrayList<>();
    ArrayList<ILineDataSet> temperatureDataSets = new ArrayList<>();
    private LineChart mChart;

    private static final AtomicInteger PROGRESS_NUM = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.data_chart_view, container, false);
        mContext = getContext();

        //mBtn = (Button) rootView.findViewById(R.id.button);
        //mBtn.setOnClickListener(mBtnOnClick);

        mChart = (LineChart) rootView.findViewById(R.id.chart);

        new LoadingMCSAsyncTask().execute(url[0]);

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
                    while ((tempStr = bufferedReader.readLine()) != null) {
                        //Log.d("csv", tempStr);
                        String[] splitStr = tempStr.split(",");

                        if(splitStr[0].equals("Temp_Display")) {
                            TemperatureData temperatureData =  new TemperatureData();
                            temperatureData.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date (Long.valueOf(splitStr[1])));
                            temperatureData.data = Float.valueOf(splitStr[2]);
                            tDatalist.add(temperatureData);
                            Log.d("csv", temperatureData.timestamp + " " + temperatureData.data);
                        }
                    }
                    bufferedReader.close();
                    inputStream.close();
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

                ArrayList<String> xAxes = new ArrayList<>();
                ArrayList<Entry> yAxes = new ArrayList<>();

                int count = 0;
                for (TemperatureData data : tDatalist) {
                    yAxes.add(new Entry(count, data.data));
                    xAxes.add(count, data.timestamp);
                    count++;
                }

                LineDataSet lineDataSet = new LineDataSet(yAxes, "Temperature");
                lineDataSet.setDrawCircles(true);
                lineDataSet.setCircleColor(R.color.colorPrimary);
                lineDataSet.setCircleRadius(8f);
                lineDataSet.setColor(R.color.colorPrimary);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(6f);
                lineDataSet.setFillColor(R.color.colorPrimary);
                lineDataSet.setDrawFilled(true);
                lineDataSet.setDrawHorizontalHighlightIndicator(false);
                lineDataSet.setDrawVerticalHighlightIndicator(false);

                temperatureDataSets.add(lineDataSet);

                // YAxis
                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setDrawGridLines(false);

                // XAxis
                CustomXAxisValue customXAxisValue = new CustomXAxisValue(xAxes);
                mChart.getXAxis().setValueFormatter(customXAxisValue);
                mChart.getXAxis().setDrawLabels(false);
                mChart.getXAxis().setDrawGridLines(false);


                mChart.getLegend().setEnabled(true);
                mChart.getAxisRight().setDrawLabels(false);
                mChart.setDrawGridBackground(false);
                mChart.setTouchEnabled(true);
                mChart.setPinchZoom(true);

                Description des = new Description();
                des.setText("");
                mChart.setDescription(des);

                MyMarkerView markerView = new MyMarkerView(rootView.getContext(), R.layout.marker_view, xAxes);

                mChart.setMarker(markerView);
                mChart.invalidate();

                mChart.setData(new LineData(temperatureDataSets));
                mChart.setVisibleXRangeMaximum(10);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    private class TemperatureData {
        String timestamp;
        float data;
    }
}
