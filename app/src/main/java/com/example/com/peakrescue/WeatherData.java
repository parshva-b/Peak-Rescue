package com.example.com.peakrescue;

public class WeatherData {
    private double temperature;
    private double humidity;
    private double precipitationProbability;
    private double windSpeed;
    private double visibility;
    private double sleetIntensity;
    private double snowIntensity;
    private double rainIntensity;

    public WeatherData(double temperature, double humidity, double precipitationProbability,
                       double windSpeed, double visibility, double sleetIntensity,
                       double snowIntensity, double rainIntensity) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitationProbability = precipitationProbability;
        this.windSpeed = windSpeed;
        this.visibility = visibility;
        this.sleetIntensity = sleetIntensity;
        this.snowIntensity = snowIntensity;
        this.rainIntensity = rainIntensity;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecipitationProbability() {
        return precipitationProbability;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getSleetIntensity() {
        return sleetIntensity;
    }

    public double getSnowIntensity() {
        return snowIntensity;
    }

    public double getRainIntensity() {
        return rainIntensity;
    }
}
