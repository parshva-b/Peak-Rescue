package com.example.com.peakrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class FinalActivity extends AppCompatActivity {

    private TextView tv_finalOutcome;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        tv_finalOutcome = findViewById(R.id.finalOutcome);
        if (true) {
            tv_finalOutcome.setTextColor(Color.GREEN);
            tv_finalOutcome.setText("Safe: You are good to go 😄👍");
        }
        else {
            tv_finalOutcome.setTextColor(Color.RED);
            tv_finalOutcome.setText("Unsafe: Consult Trek Guide ☠☠");
        }

    }
}