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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTextView = findViewById(R.id.weather_text);

        // Call API and display weather data
        startPeriodicChecks();
    }

    private void startPeriodicChecks() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> performWeatherCheck());
            }
        }, 0, 5000); // Check every 5 seconds (adjust as needed)
    }

    private void performWeatherCheck() {
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
    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            try {
                if (isNetworkAvailable()) {
                    // Replace with your API endpoint URL
                    String apiUrl = "https://api.tomorrow.io/v4/timelines?location=42.3478,-71.0466&fields=temperature,weatherCode,precipitationProbability,windSpeed,uvIndex,visibility&startTime=2023-12-01T00:00:00Z&endTime=2023-12-01T23:59:59Z&timezone=America/New_York&apikey=q4q65zHUQWmCS4w8MurpkU6JJewJhi5H";
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