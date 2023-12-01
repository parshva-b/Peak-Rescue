package com.example.project4weatherfuzzylogicandsosservice;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WeatherFuzzyLogic extends AppCompatActivity {

    private EditText editTextTemperature, editTextHumidity, editTextPrecipitationProbability,
            editTextWindSpeed, editTextVisibility, editTextSleetIntensity, editTextSnowIntensity, editTextRainIntensity;

    private Button buttonCalculate;
    private TextView textViewWeatherSeverity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_fuzzy_logic);

        // Initialize UI components
        editTextTemperature = findViewById(R.id.editTextTemperature);
        editTextHumidity = findViewById(R.id.editTextHumidity);
        editTextPrecipitationProbability = findViewById(R.id.editTextPrecipitationProbability);
        editTextWindSpeed = findViewById(R.id.editTextWindSpeed);
        editTextVisibility = findViewById(R.id.editTextVisibility);
        editTextSleetIntensity = findViewById(R.id.editTextSleetIntensity);
        editTextSnowIntensity = findViewById(R.id.editTextSnowIntensity);
        editTextRainIntensity = findViewById(R.id.editTextRainIntensity);

        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewWeatherSeverity = findViewById(R.id.textViewWeatherSeverity);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateWeatherSeverity();
            }
        });
    }

    private void calculateWeatherSeverity() {
        // Get user input
        double temperature = Double.parseDouble(editTextTemperature.getText().toString());
        double humidity = Double.parseDouble(editTextHumidity.getText().toString());
        double precipitationProbability = Double.parseDouble(editTextPrecipitationProbability.getText().toString());
        double windSpeed = Double.parseDouble(editTextWindSpeed.getText().toString());
        double visibility = Double.parseDouble(editTextVisibility.getText().toString());
        double sleetIntensity = Double.parseDouble(editTextSleetIntensity.getText().toString());
        double snowIntensity = Double.parseDouble(editTextSnowIntensity.getText().toString());
        double rainIntensity = Double.parseDouble(editTextRainIntensity.getText().toString());

        // Pass input to fuzzy controller (replace this with your actual fuzzy logic implementation)
        double weatherSeverity = WeatherFuzzyController.calculateWeatherSeverity(
                temperature, humidity, precipitationProbability, windSpeed,
                visibility, sleetIntensity, snowIntensity, rainIntensity);

        // Display the result
        textViewWeatherSeverity.setText("Weather Severity: " + weatherSeverity);
    }
}
