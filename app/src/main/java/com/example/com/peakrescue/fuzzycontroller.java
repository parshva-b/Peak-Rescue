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

    protected double weatherFuzzy() {
        return 0.0;
    }

    protected double healthFuzzy() {
        return 0.0;
    }

    protected double finalfuzzy(InputStream is1, String fileName1, Double weatherFuzzy, Double healthScore) {

        FIS fis = FIS.load(is1, true);

        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName1 + "'");
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
