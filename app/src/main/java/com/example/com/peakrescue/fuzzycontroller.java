package com.example.com.peakrescue;

import android.content.SharedPreferences;
import android.util.Log;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class fuzzycontroller {
    String heartrate, respiratoryrate, spo2, altitude, weather, symptoms;
    protected void initiateFuzzy(String allDataString ) {
        try {
            // Convert the JSON string to a JSON object
            JSONObject allData = new JSONObject(allDataString);
            heartrate = String.valueOf(allData.get("heartrate"));
            respiratoryrate = String.valueOf(allData.get("respiratoryrate"));
            spo2 = String.valueOf(allData.get("spo2"));
            altitude = String.valueOf(allData.get("altitude"));
            weather = String.valueOf(allData.get("weather"));
            symptoms = String.valueOf(allData.get("symptoms"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected double weatherFuzzy(InputStream is1, String fileName1) {
        FIS fis = FIS.load(is1, true);

        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName1 + "'");
            return 0.0;
        }
        fis.setVariable("Temperature", -10);
        fis.setVariable("Humidity", 20);
        fis.setVariable("Precipitation_Probability", 1);
        fis.setVariable("Wind_Speed", 8);
        fis.setVariable("Visibility", 4);
        fis.setVariable("SleetIntensity", 2);
        fis.setVariable("SnowIntensity", 0);
        fis.setVariable("RainIntensity",0);
        Variable rule = fis.getVariable("Severity");
        Double result = rule.getValue();
        Log.i("Added FIS output", String.valueOf(rule.getValue()));
        return result;

    }

    protected double healthFuzzy(InputStream is2, String fileName2) {

        FIS fis = FIS.load(is2, true);

        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName2 + "'");
            return 0.0;
        }
        fis.setVariable("insomnia", 0.1);
        fis.setVariable("rapid_heart_rate", 0.1);
        fis.setVariable("vomiting", 0.1);
        fis.setVariable("nausea", 0.1);
        fis.setVariable("fatigue", 0.1);
        fis.setVariable("headache", 0.1);
        fis.setVariable("shortness_of_breath", 0.1);
        fis.setVariable("travel_to_high_altitude",0.1);

        Variable rule = fis.getVariable("output1");
        Double result = rule.getValue();
        Log.i("Added FIS output", rule.toString());
        Log.i("Added FIS output", String.valueOf(rule.getValue()));
        return result;
    }

    protected double finalfuzzy(InputStream is3, String fileName3, Double weatherFuzzy, Double healthScore) {

        FIS fis = FIS.load(is3, true);

        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName3 + "'");
            return 0.0;
        }

        fis.setVariable("Altitude", Double.parseDouble(altitude));
        fis.setVariable("Health_Score", healthScore);
        fis.setVariable("Weather_Fuzzy", weatherFuzzy);
        fis.setVariable("Heart_Rate", Double.parseDouble(heartrate));
        fis.setVariable("Respiratory_Rate", Double.parseDouble(respiratoryrate));
        fis.setVariable("Oxymeter_Reading", Double.parseDouble(spo2));

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
//        Variable tip = fis.getFunctionBlock("output").getVariable("tip");

        Variable rule = fis.getVariable("To_Trek_or_not_to_trek");
        Double result = rule.getValue();
        Log.i("Added FIS output", rule.toString());
        return result;
    }
}
