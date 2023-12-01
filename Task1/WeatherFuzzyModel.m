function [WeatherSeverity] = WeatherFuzzyModel(T, H, PP, W, V, SI, WI, RI)
    % Loading the fuzzy controller
    fis = readfis('WeatherFuzzyController.fis');

    % Running the fuzzy control
    WeatherSeverity = evalfis([T, H, PP, W, V, SI, WI, RI], fis);
end