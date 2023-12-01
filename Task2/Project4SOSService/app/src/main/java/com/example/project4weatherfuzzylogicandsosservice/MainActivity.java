package com.example.project4weatherfuzzylogicandsosservice;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sosService = findViewById(R.id.sos);

        sosService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Page 2
                Intent intent = new Intent(MainActivity.this, SOSService.class);
                startActivity(intent);
            }
        });
    }
}
