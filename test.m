function [test] = WeatherFuzzyModel(Altitude, Health_Score, Weather_fuzzy, HR, RR, Oxymeter)
    % Loading the fuzzy controller
    fis = readfis('Trek_final_outcome.fis');

    % Running the fuzzy control
    test = evalfis([Altitude, Health_Score, Weather_fuzzy, HR, RR, Oxymeter], fis);
end