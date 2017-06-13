package com.example.vi8249.intelligentbasketballcourt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vi8249 on 2017/5/26.
 */

public class SplashScreenActivity extends Activity {
    private static String[] url = {
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Temp_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Hum_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Vib_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DIK4dY0L/datachannels/Vib2_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DIK4dY0L/datachannels/Temp_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DIK4dY0L/datachannels/Hum_Display/datapoints",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Vib_Display/datapoints.csv?start=1496409690000&end=" + System.currentTimeMillis() + "&limit=100",
            "https://api.mediatek.com/mcs/v2/devices/DIK4dY0L/datachannels/Vib2_Display/datapoints.csv?start=1496409690000&end=" + System.currentTimeMillis() + "&limit=100",
            "https://api.mediatek.com/mcs/v2/devices/DKV8iNT6/datachannels/Bat_Display/datapoints"
    };
    protected Activity mSplash;
    HttpURLConnection connection = null;
    String responseString = "";
    List<AsyncTask<String, Integer, Integer>> asyncTasks = new ArrayList<>();
    private String temperature = "N/A", humidity = "N/A", battery = "N/A";
    private boolean leftCourt = false, rightCourt = false;
    private ArrayList<TemperatureData> tDataList = new ArrayList<>();
    private ArrayList<HumidityData> hDataList = new ArrayList<>();
    private Intent it;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSplash = this;
        it = new Intent(mSplash, MainActivity.class);
        asyncTasks.add(new LoadingTemperatureAsyncTask().execute(url[0]));
        asyncTasks.add(new LoadingHumidityAsyncTask().execute(url[1]));
        asyncTasks.add(new LoadingLeftCourtAsyncTask().execute(url[2]));
        asyncTasks.add(new LoadingRightCourtAsyncTask().execute(url[3]));
        asyncTasks.add(new LoadingTemperatureAsyncTask().execute(url[4]));
        asyncTasks.add(new LoadingHumidityAsyncTask().execute(url[5]));
        asyncTasks.add(new LoadingTemperatureChartAsyncTask().execute(url[6]));
        asyncTasks.add(new LoadingHumidityChartMCSAsyncTask().execute(url[7]));
        asyncTasks.add(new LoadingBatteryAsyncTask().execute(url[8]));
    }

    private void chooseViewToUpdate(JsonObject tJsonObject) {
        switch (tJsonObject.getDataChannels().get(0).getDataChnID()) {
            case "Temp_Display":
                temperature = Float.toString(tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue());
                break;
            case "Hum_Display":
                humidity = Float.toString(tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue());
                break;
            case "Vib_Display":
                leftCourt = (int) tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue() == 0;
                break;
            case "Vib2_Display":
                rightCourt = (int) tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue() == 0;
                break;
            case "Bat_Display":
                battery = Float.toString(tJsonObject.getDataChannels().get(0).getDataPoints().get(0).getValues().getValue());
                break;
        }
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
                    StringBuffer stringBuffer = new StringBuffer();

                    while ((tempStr = bufferedReader.readLine()) != null) {
                        stringBuffer.append(tempStr);
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
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String deviceId = "";
            if (invalidUrl) {
                temperature = "N/A";
            } else {
                Gson gson = new Gson();
                JsonObject tJsonObject = gson.fromJson(responseString, JsonObject.class);
                chooseViewToUpdate(tJsonObject);
                deviceId = tJsonObject.getDeviceID();
            }
            if (deviceId.equals("DKV8iNT6")) {
                it.putExtra("court1Temperature", temperature);
                boolean check = true;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                    if (i != 0)
                        check = asyncTaskItem.getStatus() == Status.FINISHED;
                }
                if (check) {
                    startActivity(it);
                    finish();
                }
            } else if (deviceId.equals("DIK4dY0L")) {
                it.putExtra("court2Temperature", temperature);
                boolean check = false;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                    if (i != 4)
                        check = asyncTaskItem.getStatus() == Status.FINISHED;
                }
                if (check) {
                    startActivity(it);
                    finish();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    private class LoadingHumidityAsyncTask extends AsyncTask<String, Integer, Integer> {
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
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String deviceId = "";
            if (invalidUrl) {
                humidity = "N/A";
            } else {
                Gson gson = new Gson();
                JsonObject tJsonObject = gson.fromJson(responseString, JsonObject.class);
                chooseViewToUpdate(tJsonObject);
                deviceId = tJsonObject.getDeviceID();
            }
            if (deviceId.equals("DKV8iNT6")) {
                it.putExtra("court1Humidity", humidity);
                boolean check = false;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                    if (i != 1)
                        check = asyncTaskItem.getStatus() == Status.FINISHED;
                }
                if (check) {
                    startActivity(it);
                    finish();
                }
            } else if (deviceId.equals("DIK4dY0L")) {
                it.putExtra("court2Humidity", humidity);
                boolean check = false;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                    if (i != 5)
                        check = asyncTaskItem.getStatus() == Status.FINISHED;
                }
                if (check) {
                    startActivity(it);
                    finish();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class LoadingLeftCourtAsyncTask extends AsyncTask<String, Integer, Integer> {
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
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String deviceId = "";
            if (invalidUrl) {
                leftCourt = false;
            } else {
                Gson gson = new Gson();
                JsonObject tJsonObject = gson.fromJson(responseString, JsonObject.class);
                chooseViewToUpdate(tJsonObject);
                deviceId = tJsonObject.getDeviceID();
            }
            if (deviceId.equals("DKV8iNT6")) {
                it.putExtra("court1LeftCourt", leftCourt);
                boolean check = false;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                    if (i != 2)
                        check = asyncTaskItem.getStatus() == Status.FINISHED;
                }
                if (check) {
                    startActivity(it);
                    finish();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class LoadingRightCourtAsyncTask extends AsyncTask<String, Integer, Integer> {
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
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String deviceId = "";
            if (invalidUrl) {
                rightCourt = false;
            } else {
                Gson gson = new Gson();
                JsonObject tJsonObject = gson.fromJson(responseString, JsonObject.class);
                chooseViewToUpdate(tJsonObject);
                deviceId = tJsonObject.getDeviceID();
            }
            if (deviceId.equals("DIK4dY0L")) {
                it.putExtra("court1RightCourt", rightCourt);
                boolean check = false;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                    if (i != 3)
                        check = asyncTaskItem.getStatus() == Status.FINISHED;
                }
                if (check) {
                    startActivity(it);
                    finish();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class LoadingTemperatureChartAsyncTask extends AsyncTask<String, Integer, Integer> {
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
                        String[] splitStr = tempStr.split(",");

                        if (splitStr[0].equals("Vib_Display")) {
                            TemperatureData temperatureData = new TemperatureData();
                            temperatureData.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(Long.valueOf(splitStr[1])));
                            temperatureData.data = Float.valueOf(splitStr[2]);
                            tDataList.add(temperatureData);
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
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (!invalidUrl) {
                it.putExtra("temperatureList", tDataList);
            }
            boolean check = false;
            for (int i = 0; i < asyncTasks.size(); i++) {
                AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                if (i != 6)
                    check = asyncTaskItem.getStatus() == Status.FINISHED;
            }
            if (check) {
                startActivity(it);
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class LoadingHumidityChartMCSAsyncTask extends AsyncTask<String, Integer, Integer> {
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
                        String[] splitStr = tempStr.split(",");

                        if (splitStr[0].equals("Vib2_Display")) {
                            HumidityData humidityData = new HumidityData();
                            humidityData.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(Long.valueOf(splitStr[1])));
                            humidityData.data = Float.valueOf(splitStr[2]);
                            hDataList.add(humidityData);
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
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (!invalidUrl) {
                it.putExtra("humidityList", hDataList);
            }
            boolean check = false;
            for (int i = 0; i < asyncTasks.size(); i++) {
                AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                if (i != 7)
                    check = asyncTaskItem.getStatus() == Status.FINISHED;
            }
            if (check) {
                startActivity(it);
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class LoadingBatteryAsyncTask extends AsyncTask<String, Integer, Integer> {
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
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String deviceId = "";
            if (invalidUrl) {
                battery = "N/A";
            } else {
                Gson gson = new Gson();
                JsonObject tJsonObject = gson.fromJson(responseString, JsonObject.class);
                chooseViewToUpdate(tJsonObject);
                deviceId = tJsonObject.getDeviceID();
            }
            if (deviceId.equals("DKV8iNT6")) {
                it.putExtra("court1Battery", battery);
                boolean check = false;
                for (int i = 0; i < asyncTasks.size(); i++) {
                    AsyncTask<String, Integer, Integer> asyncTaskItem = asyncTasks.get(i);
                    if (i != 8)
                        check = asyncTaskItem.getStatus() == Status.FINISHED;
                }
                if (check) {
                    startActivity(it);
                    finish();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
