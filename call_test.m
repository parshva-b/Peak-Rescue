Altitude = 0;
Health_Score = 0.2;
Weather_fuzzy = 98;
HR = 80; 
RR = 16;
Oxymeter = 0;

assert(test(Altitude, Health_Score, Weather_fuzzy, HR, RR, Oxymeter) < 1, 'failed');
HR = 20;
RR = 10;
Oxymeter = 100;
assert(test(Altitude, Health_Score, Weather_fuzzy, HR, RR, Oxymeter) < 0.5, 'failed');
disp('done');