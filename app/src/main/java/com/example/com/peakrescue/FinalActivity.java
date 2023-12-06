package com.example.com.peakrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class FinalActivity extends AppCompatActivity {

    private TextView tv_finalOutcome;
    private double final_fuzzy;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        tv_finalOutcome = findViewById(R.id.finalOutcome);

        SharedPreferences sharedPreferences = getSharedPreferences("allData", MODE_PRIVATE);
        // Retrieve existing weather data as a JSON string
        String allDataString = sharedPreferences.getString("allData", "{}");

        try {
            // Convert the JSON string to a JSON object
            JSONObject allData = new JSONObject(allDataString);
            final_fuzzy = Double.parseDouble(String.valueOf(allData.get("finalfuzzy")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (final_fuzzy > 0.22) {
            tv_finalOutcome.setTextColor(Color.GREEN);
            tv_finalOutcome.setText("Safe: You are good to go 😄👍");
        } else {
            tv_finalOutcome.setTextColor(Color.RED);
            tv_finalOutcome.setText("Unsafe: Consult Trek Guide ☠☠");
        }
    }
}