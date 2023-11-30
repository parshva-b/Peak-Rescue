package com.example.peakrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView weatherTextView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Timer timer = new Timer();
    private EditText latitudeInput;
    private EditText longitudeInput;
    private EditText dateInput;
    private Button getWeatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitudeInput = findViewById(R.id.latitude_input);
        longitudeInput = findViewById(R.id.longitude_input);
        dateInput = findViewById(R.id.date_input);
        getWeatherButton = findViewById(R.id.get_weather_button);
        weatherTextView = findViewById(R.id.weather_text);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = latitudeInput.getText().toString();
                String longitude = longitudeInput.getText().toString();
                String date = dateInput.getText().toString();

                // Call method to fetch weather data with provided location and date
                new FetchWeatherTask().execute(latitude, longitude, date);
            }
        });
//        startPeriodicChecks();
    }

    private void updateStoredDataPeriodically() {
        if (isNetworkAvailable()) {
            new FetchWeatherTask().execute();
        }
    }
//    private void startPeriodicChecks() {
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(() -> {
//                    performWeatherCheck();
//                    updateStoredDataPeriodically();
//                });
//            }
//        }, 0, 3600000); // 1 hour (adjust as needed, in milliseconds)
//    }

    private void performWeatherCheck() {
        Log.d("internetconn:", "val:"+isNetworkAvailable());
        if (isNetworkAvailable()) {
            // If there's internet, execute the AsyncTask to fetch weather data
            new FetchWeatherTask().execute();
        } else {
            // If there's no internet, display stored data from SharedPreferences
            String storedWeatherData = getStoredWeatherData();
            if (!storedWeatherData.isEmpty()) {
                // Display stored data from SharedPreferences
                weatherTextView.setText("No internet signal\n" + storedWeatherData);
            } else {
                // No stored data available in SharedPreferences
                weatherTextView.setText("No internet signal\nNo stored data");
            }
        }
    }
    private String getStoredWeatherData() {
        SharedPreferences preferences = getSharedPreferences("WeatherData", MODE_PRIVATE);
        return preferences.getString("data", "");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void saveWeatherData(String weatherData) {
        SharedPreferences preferences = getSharedPreferences("WeatherData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("data", weatherData);
        editor.apply();
    }
    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String ...params) {
            String result = null;
            String latitude = params[0];
            String longitude = params[1];
            String date = params[2];
            try {
                if (isNetworkAvailable()) {
                    // Replace with your API endpoint URL
                    String apiUrl = "https://api.tomorrow.io/v4/timelines?location=" + latitude + "," + longitude +
                            "&fields=temperature,weatherCode,precipitationProbability,windSpeed,uvIndex,visibility" +
                            "&startTime=" + date + "T00:00:00Z&endTime=" + date + "T01:00:00Z&timezone=America/New_York&apikey=q4q65zHUQWmCS4w8MurpkU6JJewJhi5H";
                    Log.d("url: ", apiUrl);
                    URL url = new URL(apiUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    result = stringBuilder.toString();
                    saveWeatherData(result);
                }
            }    catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    // Get the necessary weather information from the JSON response
                    String weatherInfo = jsonObject.toString(); // Replace with the specific data you want to display

                    // Display weather information in TextView
                    weatherTextView.setText(weatherInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                // If result is null, show a message indicating no internet signal
                String storedWeatherData = getStoredWeatherData();
                if (!storedWeatherData.isEmpty()) {
                    // Display stored data from SharedPreferences
                    weatherTextView.setText("No internet signal\n" + storedWeatherData);
                } else {
                    // No stored data available in SharedPreferences
                    weatherTextView.setText("No internet signal\nNo stored data");
                }
            }
        }




    }
}