package com.example.com.peakrescue;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button bt_new_trek;
    private Button bt_exst_trek;
    private Button bt_sos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_new_trek = findViewById(R.id.startNewTrekButton);
        bt_exst_trek = findViewById(R.id.checkInExistingTrekButton);
        bt_sos = findViewById(R.id.sosButton);
        SharedPreferences sharedPreferences = getSharedPreferences("allData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Create a JSON object for weather data
        JSONObject allData = new JSONObject();
        try {
            // Initialize all attributes to null
            allData.put("weather", JSONObject.NULL);
            allData.put("heartrate", JSONObject.NULL);
            allData.put("respiratoryrate", JSONObject.NULL);
            allData.put("altitude", JSONObject.NULL);
            allData.put("spo2", JSONObject.NULL);
            allData.put("symptoms", JSONObject.NULL);
            allData.put("fuzzy1output", JSONObject.NULL);
            allData.put("fuzzy2output", JSONObject.NULL);
            allData.put("finalfuzzy", JSONObject.NULL);

            // Convert JSON object to string and save in SharedPreferences
            editor.putString("allData", allData.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bt_new_trek.setOnClickListener(v -> start_new_trek());
        bt_exst_trek.setOnClickListener(v -> check_exst_trek());
        bt_sos.setOnClickListener(v -> sosActivity());
    }

    private void start_new_trek() {
        Intent i = new Intent(this, activity_weather.class);
        startActivity(i);
    }

    private void check_exst_trek() {
        Intent i = new Intent(this, activity_weather.class);
        startActivity(i);
    }

    private void sosActivity() {
        Intent i = new Intent(this, SOS_Activity.class);
        startActivity(i);
    }

}