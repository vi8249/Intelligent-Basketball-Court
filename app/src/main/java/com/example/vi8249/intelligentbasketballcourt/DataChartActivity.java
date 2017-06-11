package com.example.vi8249.intelligentbasketballcourt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vi8249 on 2017/6/3.
 */

public class DataChartActivity extends Fragment {
    private static final AtomicInteger PROGRESS_NUM = new AtomicInteger(0);
    private static final AtomicInteger CONNECTION_NUM = new AtomicInteger(0);
    HttpURLConnection connection = null;
    private String[] url = {
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Temp_Display/datapoints.csv?start=1496409690000&end=" + System.currentTimeMillis() + "&limit=100",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Hum_Display/datapoints.csv?start=1496409690000&end=" + System.currentTimeMillis() + "&limit=100"
    };
    private ProgressDialog pDialog;
    private TextView textView, textView2;
    private Button mBtn, mBtn2;
    private View rootView;
    private String startTime, endTime;
    private Date date1, date2;
    private LineChart mChart;
    private ArrayList<TemperatureData> tDataList = new ArrayList<>();
    private ArrayList<HumidityData> hDataList = new ArrayList<>();
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private int mYear, mMonth, mDay;

    private View.OnClickListener mBtnOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    String format = setDateFormat(year, month, day);
                    textView.setText(format);

                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date1 = formatter.parse(format);
                        startTime = "" + date1.getTime();
                        Log.d("timestamp", "" + date1.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (!textView2.getText().equals("End Time")) {
                        if (date1.getTime() >= date2.getTime()) {
                            new android.app.AlertDialog.Builder(getActivity())
                                    .setTitle("Warning")
                                    .setMessage("InValid Input!\n End Time must bigger than Start Time")
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        } else {
                            setUrl();

                            mChart.clearValues();
                            new DataChartActivity.LoadingTemperatureAsyncTask().execute(url[0]);
                            new DataChartActivity.LoadingHumidityMCSAsyncTask().execute(url[1]);
                        }
                    }
                }

            }, mYear, mMonth, mDay).show();
        }
    };

    private View.OnClickListener mBtnOnClick2 = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    String format = setDateFormat(year, month, day);
                    textView2.setText(format);

                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date2 = formatter.parse(format);
                        endTime = "" + date2.getTime();
                        Log.d("timestamp", "" + date2.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (!textView.getText().equals("Start Time")) {
                        if (date1.getTime() >= date2.getTime()) {
                            new android.app.AlertDialog.Builder(getActivity())
                                    .setTitle("Warning")
                                    .setMessage("InValid Input!\n End Time must bigger than Start Time")
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        } else {
                            setUrl();

                            mChart.clearValues();
                            new DataChartActivity.LoadingTemperatureAsyncTask().execute(url[0]);
                            new DataChartActivity.LoadingHumidityMCSAsyncTask().execute(url[1]);
                        }
                    }
                }
            }, mYear, mMonth, mDay).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.data_chart_view, container, false);

        textView = (TextView) rootView.findViewById(R.id.textView);
        textView2 = (TextView) rootView.findViewById(R.id.textView2);
        mBtn = (Button) rootView.findViewById(R.id.button);
        mBtn.setOnClickListener(mBtnOnClick);
        mBtn2 = (Button) rootView.findViewById(R.id.button2);
        mBtn2.setOnClickListener(mBtnOnClick2);

        InitialLineChart();

        new DataChartActivity.LoadingTemperatureAsyncTask().execute(url[0]);
        new DataChartActivity.LoadingHumidityMCSAsyncTask().execute(url[1]);

        return rootView;
    }

    private void InitialLineChart() {
        mChart = (LineChart) rootView.findViewById(R.id.chart);
    }

    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        return String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);
    }

    private void setUrl() {
        String tUrls[] = {
                "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Temp_Display/datapoints.csv?start=:startTime&end=:endTime&limit=100",
                "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Hum_Display/datapoints.csv?start=:startTime&end=:endTime&limit=100",
        };
        for (int i = 0; i < tUrls.length; i++) {
            String s = tUrls[i].replace(":startTime", startTime);
            url[i] = s;
            String e = url[i].replace(":endTime", endTime);
            url[i] = e;
        }
    }

    private void addLineDataSet() {
        ArrayList<String> xAxes = new ArrayList<>();
        ArrayList<Entry> yAxes = new ArrayList<>();
        ArrayList<String> xAxes2 = new ArrayList<>();
        ArrayList<Entry> yAxes2 = new ArrayList<>();

        int count = 0;
        for (TemperatureData data : tDataList) {
            yAxes.add(new Entry(count, data.data));
            xAxes.add(count, data.timestamp);
            count++;
        }

        count = 0;
        for (HumidityData data : hDataList) {
            yAxes2.add(new Entry(count, data.data));
            xAxes2.add(count, data.timestamp);
            count++;
        }

        LineDataSet lineDataSet = new LineDataSet(yAxes, "Temperature");
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setCircleRadius(7f);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(6f);
        lineDataSet.setFillColor(Color.BLUE);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawVerticalHighlightIndicator(false);

        LineDataSet lineDataSet2 = new LineDataSet(yAxes2, "Humidity");
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setCircleColor(Color.RED);
        lineDataSet2.setCircleRadius(7f);
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setLineWidth(6f);
        lineDataSet2.setFillColor(Color.RED);
        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawVerticalHighlightIndicator(false);

        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet2);

        // YAxis
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaximum(100f);
        leftAxis.setDrawGridLines(false);

        // XAxis
        CustomXAxisValue customXAxisValue;
        MyMarkerView markerView;
        if (xAxes.size() > xAxes2.size()) {
            customXAxisValue = new CustomXAxisValue(xAxes);
            markerView = new MyMarkerView(rootView.getContext(), R.layout.marker_view, xAxes);
        } else {
            customXAxisValue = new CustomXAxisValue(xAxes2);
            markerView = new MyMarkerView(rootView.getContext(), R.layout.marker_view, xAxes2);
        }

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

        mChart.setMarker(markerView);
        mChart.invalidate();

        mChart.setData(new LineData(dataSets));
        mChart.setVisibleXRangeMaximum(10);
    }

    private class LoadingTemperatureAsyncTask extends AsyncTask<String, Integer, Integer> {
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

                        if (splitStr[0].equals("Temp_Display")) {
                            TemperatureData temperatureData = new TemperatureData();
                            temperatureData.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(Long.valueOf(splitStr[1])));
                            temperatureData.data = Float.valueOf(splitStr[2]);
                            tDataList.add(temperatureData);
                            //Log.d("csv", temperatureData.timestamp + " " + temperatureData.data);
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
            if (x == 0) {
                pDialog = new ProgressDialog(getActivity());
                pDialog = ProgressDialog.show(getActivity(), "Message", "Loading...", true);
                pDialog.setCancelable(false);
                tDataList.clear();
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
                if (CONNECTION_NUM.getAndIncrement() == 0) {
                    new android.app.AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage("Connection failed")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CONNECTION_NUM.decrementAndGet();
                                }
                            })
                            .show();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class LoadingHumidityMCSAsyncTask extends AsyncTask<String, Integer, Integer> {
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

                        if (splitStr[0].equals("Hum_Display")) {
                            HumidityData humidityData = new HumidityData();
                            humidityData.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(Long.valueOf(splitStr[1])));
                            humidityData.data = Float.valueOf(splitStr[2]);
                            hDataList.add(humidityData);
                            //Log.d("csv", humidityData.timestamp + " " + humidityData.data);
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
            if (x == 0) {
                pDialog = new ProgressDialog(getActivity());
                pDialog = ProgressDialog.show(getActivity(), "Message", "Loading...", true);
                pDialog.setCancelable(false);
                hDataList.clear();
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
                if (CONNECTION_NUM.getAndIncrement() == 0) {
                    new android.app.AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage("Connection failed")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CONNECTION_NUM.decrementAndGet();
                                }
                            })
                            .show();
                }
            } else {
                addLineDataSet();
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

    private class HumidityData {
        String timestamp;
        float data;
    }
}
