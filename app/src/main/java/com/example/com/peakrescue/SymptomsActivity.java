package com.example.com.peakrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import com.example.com.peakrescue.fuzzycontroller;

public class SymptomsActivity extends AppCompatActivity {

    RatingBar lv_rating;                                                                          // Local variable for symptoms rating
    Map<String, Float> Symptoms = new HashMap<>();                                                // Hashmap for storing symptoms
    ArrayAdapter<CharSequence> lv_adapter;                                                        // Local variable for array adapter for symptoms
    Spinner lv_symptoms_list;                                                                     // Local variable for spinner(drop down)
    Button bt_nextActivity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        bt_nextActivity = findViewById(R.id.bt_next);

        Resources res = getResources();                                                           // Returns resource instance for this application
        lv_adapter = ArrayAdapter.createFromResource(this,
                R.array.symptoms_array, android.R.layout.simple_spinner_item);
        lv_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        // Assigning symptoms values to the spinner for dropdown

        for (String symp: res.getStringArray(R.array.symptoms_array) )
        {
//          Initiating symptoms and rating 0 for all the symptoms
            Symptoms.put(symp, 0f);
        }

//      Final one element array required as from the inner class you can't assign value to a
//      local variable (itself) declared somewhere in the enclosing class.
        final String[] selected_symptom = new String[1];
        lv_rating = findViewById(R.id.rating_symptom);                                            // Rating for symptoms

        lv_symptoms_list = findViewById(R.id.sp_symptoms);                                        // Symptoms dropdown
        lv_symptoms_list.setAdapter(lv_adapter);                                                  // setting the dropdown list for adapter

//      Action on item selected from the drop down list
        lv_symptoms_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                      Get the item from the drop down based on the position
                Object item = adapterView.getItemAtPosition(i);
//                      Get the string of item selected
                selected_symptom[0] = item.toString();
//                      Get the value of selected item from the dictionary array
                lv_rating.setRating(Symptoms.get(selected_symptom[0]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                      Do Nothing
            }
        });

//              Action on change in rating
        lv_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//                      Get the star rating
                float rating = lv_rating.getRating();
//                      Update the rating in the dictionary against the drop down selected item
                Symptoms.put(selected_symptom[0], rating);
            }
        });

        bt_nextActivity.setOnClickListener(v -> {
            try {
                nextActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    private void nextActivity() throws IOException {
        setSharedPrefData();
        String fileName1 = "WeatherFuzzy.fcl";
        InputStream is1 = getApplicationContext().getAssets().open(fileName1);
        String fileName2 = "HealthFuzzy.fcl";
        InputStream is2 = getApplicationContext().getAssets().open(fileName2);
        String fileName3 = "ToTrekOrNot.fcl";
        InputStream is3 = getApplicationContext().getAssets().open(fileName3);

        SharedPreferences sharedPreferences = getSharedPreferences("allData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String allDataString = sharedPreferences.getString("allData", "{}");

        fuzzycontroller fc = new fuzzycontroller();
        fc.initiateFuzzy(allDataString);

//        Double weatherfuzzy = fc.weatherFuzzy(is1, fileName1);
//        Double healthScore = fc.healthFuzzy(is2, fileName2);
//        Double final_decision = fc.finalfuzzy(is3, fileName3, weatherfuzzy, healthScore);
        Double final_decision = 0.33;
//        setfinalSharedPrefData(weatherfuzzy, healthScore, final_decision);
        setfinalSharedPrefData(0.22, 0.22, final_decision);
        Intent i = new Intent(this, FinalActivity.class);
        startActivity(i);
    }

    private void setSharedPrefData() {
        SharedPreferences sharedPreferences = getSharedPreferences("allData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve existing weather data as a JSON string
        String allDataString = sharedPreferences.getString("allData", "{}");

        try {
            // Convert the JSON string to a JSON object
            JSONObject allData = new JSONObject(allDataString);

            // Update the attribute with the new value
            allData.put("symptoms", new JSONObject(Symptoms));

            // Save the updated JSON object as a string in SharedPreferences
            editor.putString("allData", allData.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setfinalSharedPrefData(Double weatherfuzzy, Double healthScore, Double final_decision) {
        SharedPreferences sharedPreferences = getSharedPreferences("allData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve existing weather data as a JSON string
        String allDataString = sharedPreferences.getString("allData", "{}");

        try {
            // Convert the JSON string to a JSON object
            JSONObject allData = new JSONObject(allDataString);

            // Update the attribute with the new value
            allData.put("fuzzy1output", String.valueOf(weatherfuzzy));
            allData.put("fuzzy2output", String.valueOf(healthScore));
            allData.put("finalfuzzy", String.valueOf(final_decision));

            // Save the updated JSON object as a string in SharedPreferences
            editor.putString("allData", allData.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}