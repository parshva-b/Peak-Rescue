package com.example.com.peakrescue;

import android.content.SharedPreferences;
import android.util.Log;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

public class fuzzycontroller {
    String heartrate, respiratoryrate, spo2, altitude, weather;
    JSONObject symptoms;
    HashMap<String, String> symphashMap = new HashMap<>();
    protected void initiateFuzzy(String allDataString ) {
        try {
            // Convert the JSON string to a JSON object
            JSONObject allData = new JSONObject(allDataString);
            heartrate = String.valueOf(allData.get("heartrate"));
            respiratoryrate = String.valueOf(allData.get("respiratoryrate"));
            spo2 = String.valueOf(allData.get("spo2"));
            altitude = String.valueOf(allData.get("altitude"));
            weather = String.valueOf(allData.get("weather"));
            symptoms = new JSONObject(String.valueOf(allData.get("symptoms")));

            // Convert JSONObject to HashMap

            // Iterate through the keys and values in the JSONObject
            Iterator<String> keys = symptoms.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = symptoms.getString(key);
                symphashMap.put(key, value);

//            Log.i("Symptoms", symptoms);
//            System.out.println(symptoms);

            }
        }catch(JSONException e){
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

        WeatherData wData = null;
        try {
            wData = WeatherDataParser.parseWeatherData(weather);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        fis.setVariable("Temperature", wData.getTemperature());
        fis.setVariable("Humidity", wData.getHumidity());
        fis.setVariable("Precipitation_Probability", wData.getPrecipitationProbability());
        fis.setVariable("Wind_Speed", wData.getWindSpeed());
        fis.setVariable("Visibility", wData.getVisibility());
        fis.setVariable("SleetIntensity", wData.getSleetIntensity());
        fis.setVariable("SnowIntensity", wData.getSnowIntensity());
        fis.setVariable("RainIntensity", wData.getRainIntensity());
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

//        <string-array name="symptoms_array">
//        <item>Travel to High Altitude</item>
//        <item>Shortness of Breath</item>
//        <item>Headache</item>
//        <item>Fatigue</item>
//        <item>Nausea</item>
//        <item>Vomiting</item>
//        <item>Rapid Heart Rate</item>
//        <item>Insomnia</item>
//    </string-array>


        fis.setVariable("insomnia", Double.parseDouble(symphashMap.get("Insomnia")));
        fis.setVariable("rapid_heart_rate", Double.parseDouble(symphashMap.get("Rapid Heart Rate")));
        fis.setVariable("vomiting", Double.parseDouble(symphashMap.get("Vomiting")));
        fis.setVariable("nausea", Double.parseDouble(symphashMap.get("Nausea")));
        fis.setVariable("fatigue", Double.parseDouble(symphashMap.get("Fatigue")));
        fis.setVariable("headache", Double.parseDouble(symphashMap.get("Headache")));
        fis.setVariable("shortness_of_breath", Double.parseDouble(symphashMap.get("Shortness of Breath")));
        fis.setVariable("travel_to_high_altitude",Double.parseDouble(symphashMap.get("Travel to High Altitude")));

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
