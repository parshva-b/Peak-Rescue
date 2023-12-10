package com.example.com.peakrescue;

import org.json.JSONException;
import org.json.JSONObject;
public class WeatherDataParser {
    public static WeatherData parseWeatherData(String jsonString) throws JSONException {
        jsonString = "{\"data\":{\"time\":\"2023-11-30T01:49:00Z\",\"values\":{\"cloudBase\":1.04,\"cloudCeiling\":null,\"cloudCover\":18,\"dewPoint\":8.63,\"freezingRainIntensity\":0,\"humidity\":53,\"precipitationProbability\":0,\"pressureSurfaceLevel\":1015.09,\"rainIntensity\":0,\"sleetIntensity\":0,\"snowIntensity\":0,\"temperature\":0.81,\"temperatureApparent\":1.12,\"uHealthConcern\":0,\"uvIndex\":0,\"visibility\":16,\"weatherCode\":1100,\"windDirection\":283,\"windGust\":4.63,\"windSpeed\":1.69}},\"location\":{\"lat\":42.3478,\"Ion\":71.0466}}";

        JSONObject json = new JSONObject(jsonString);

        JSONObject data = json.getJSONObject("data");
        JSONObject values = data.getJSONObject("values");

        double temperature = values.optDouble("temperature");
        double humidity = values.optDouble("humidity");
        double precipitationProbability = values.optDouble("precipitationProbability");
        double windSpeed = values.optDouble("windSpeed");
        double visibility = values.optDouble("visibility");
        double sleetIntensity = values.optDouble("sleetIntensity");
        double snowIntensity = values.optDouble("snowIntensity");
        double rainIntensity = values.optDouble("rainIntensity");

        return new WeatherData(temperature, humidity, precipitationProbability, windSpeed, visibility,
                sleetIntensity, snowIntensity, rainIntensity);
    }

}
