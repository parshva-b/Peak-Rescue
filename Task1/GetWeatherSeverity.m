% Inputs from Weather Forecast model
Temperature = 1.5;
Humidity = 53;
PrecipitationProbability = 1;
WindSpeed = 1.69;
Visibility = 16;
SleetIntensity = 20;
SnowIntensity = 0;
RainIntensity = 0;

% Running 
[WeatherSeverity] = WeatherFuzzyModel(Temperature, Humidity, PrecipitationProbability, WindSpeed, Visibility, SleetIntensity, SnowIntensity, RainIntensity);

disp(WeatherSeverity);