package com.example.com.peakrescue;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

//  Constants
    private static final int CAMERA_REQUEST = 9999;

//  Widgets
//  For Altitude
    private Button bt_get_altitude;
    private LocationManager locationManager;
    private TextView tv_altitude;

//  For Heart Rate
    private Button bt_measure_heart_rate;
    private CameraManager mCameraManager;
    private ExecutorService executorService;
    private TextView tv_hear_rate_display;
    private VideoView vv_heart_rate_display;

//  For Respiratory Rate
    private Button bt_measure_resp_rate;
    private TextView tv_resp_rate_display;
    private ArrayList<Float> accelX;                                                              // Local variable for X coordinate of accelerometer
    private ArrayList<Float> accelY;                                                              // Local variable for Y coordinate of accelerometer
    private ArrayList<Float> accelZ;                                                              // Local variable for Z coordinate of accelerometer
    private SensorManager sensorManager;                                                          // Local sensor manager for accelerometer
    private Sensor accelerometer;                                                                 // Declaring accelerometer sensor
    private SensorEventListener sensorEventListener;                                              // Local sensor event listener for accelerometer
    private boolean isSensorRunning = false;                                                      // Sensor active flag
    private CountDownTimer timer;                                                                 // Countdown timer for accelerometer

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize_widgets();

        bt_get_altitude.setOnClickListener(v -> get_altitude());
        bt_measure_heart_rate.setOnClickListener(v -> measure_heart_rate());
        bt_measure_resp_rate.setOnClickListener(v -> measure_resp_rate());

        //      Recording the accelerometer values on sensor changes
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                accelX.add(event.values[0]);                                                      // X coordinate
                accelY.add(event.values[1]);                                                      // Y coordinate
                accelZ.add(event.values[2]);                                                      // Z coordinate
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Do nothing
            }
        };

//      45 seconds timer for accelerometer with reading taken every 1 sec
        timer = new CountDownTimer(45000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//              Displaying the timer on screen
                long secondsRemaining = millisUntilFinished / 1000;
                tv_resp_rate_display.setText(secondsRemaining + " seconds");
            }

            @Override
            public void onFinish() {
//              After 45 seconds stop the accelerometer
                stopSensor();
            }
        };

    }

    protected void initialize_widgets(){

//      For Altitude
        bt_get_altitude = findViewById(R.id.bt_get_altitude);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tv_altitude = findViewById(R.id.tv_altitude);

//      For Heart Rate
        bt_measure_heart_rate = findViewById(R.id.bt_measure_heart_rate);
        tv_hear_rate_display = findViewById(R.id.tv_heart_rate);
        vv_heart_rate_display = findViewById(R.id.vv_heart_rate);
//      Getting a thread from the pool
        executorService = Executors.newCachedThreadPool();
//      Getting handler for Camera services
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
//          Getting the list of currently connected cameras
            String mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

//      For Respiratory Rate
        bt_measure_resp_rate = findViewById(R.id.bt_measure_respiratory_rate);
        tv_resp_rate_display = findViewById(R.id.tv_respiratory_rate);
//      Constructing Array list for accelerometer coordinates
        accelX = new ArrayList<Float>();                                                          // X Coordinate
        accelY = new ArrayList<Float>();                                                          // Y Coordinate
        accelZ = new ArrayList<Float>();                                                          // Z Coordinate
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);                 // Sensor manager for accelerometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);                // Get the accelerometer sensor
    }

    protected void get_altitude(){
        if (locationManager != null){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null){
                    double altitude = lastKnownLocation.getAltitude();
                    tv_altitude.setText(Double.toString(altitude));
                }
                else {
                    tv_altitude.setText("Unable to fetch location");
                }
            }
            else {
                tv_altitude.setText("Enable location access for this application");
            }
        }
    }

    protected void measure_heart_rate(){
        Toast.makeText(getApplicationContext(), "Please turn on the flash, place your finger on camera and press the record button. A 45 sec video will be recorded", Toast.LENGTH_LONG).show();
        start_camera_process();
    }

    protected void start_camera_process(){
//      Intent for video capture
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

//      Limiting the video duration for 45 seconds
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);

//      Start the capture activity
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//      If the feed is from the expected camera source
        if (requestCode == CAMERA_REQUEST) {

//          Set the obtained uri to display in video view
            vv_heart_rate_display.setVideoURI(data.getData());
//          Set audio to off while playing
            vv_heart_rate_display.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
//          start the video play
            vv_heart_rate_display.start();

//          Calculating hear rate message
            tv_hear_rate_display.setText("Calculating...");

//          Method to calculate the heart rate
            measure_heart_rate(data.getData());
        }
    }

    private void measure_heart_rate(Uri data) {
        final String[] h_rate = new String[1];

        Handler handler = new Handler(Looper.getMainLooper());

        Runnable uiRunnable = new Runnable() {
            @Override
            public void run() {

//              Update hear rate in the text view
                tv_hear_rate_display.setText(h_rate[0]);
            }
        };

        Runnable bgRunnable = new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        h_rate[0] = calculate_h_rate(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    handler.post(uiRunnable);
                }
            }
        };

        executorService.submit(bgRunnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private String calculate_h_rate(Uri data) throws IOException {
//      Retriever for media files
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//      Array list for storing the frames from video
        List<Bitmap> frameList = new ArrayList<>();
        try {
//          Set the uri to retriever
            retriever.setDataSource(this, data);

//          Get the frame count of the video
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
            int aduration = Integer.parseInt(duration);
            int i = 0;                                                                            // frame counter
            while (i < aduration) {
                Log.i("Heart rate", String.valueOf(i));
                Bitmap bitmap = null;
                bitmap = retriever.getFrameAtIndex(i);                                            // get the bitmap of frame at given index
                frameList.add(bitmap);
                i += 5;                                                                           // increment the counter
            }
        } catch (Exception m_e) {
        } finally {
            if (retriever != null) {
//              Release the the retriever
                retriever.release();
            }
            long redBucket = 0;
            long pixelCount = 0;
            List<Long> a = new ArrayList<>();
            for (Bitmap i : frameList) {
                redBucket = 0;
//              height from 550 to 650
                for (int y = 550; y < 650; y++) {
//                  wigth from 550 to 650
                    for (int x = 550; x < 650; x++) {
//                      get pixel
                        int c = i.getPixel(x, y);
                        pixelCount += 1;
                        redBucket += Color.red(c) + Color.blue(c) + Color.green(c);
                    }
                }
                a.add(redBucket);
            }
            List<Long> b = new ArrayList<>();
            for (int i = 0; i < a.size() - 5; i++) {
//              Get average
                long temp = (a.get(i) + a.get(i + 1) + a.get(i + 2) + a.get(i + 3) + a.get(i + 4)) / 4;
                b.add(temp);
            }
            long x = b.get(0);
            int count = 0;
            for (int i = 1; i < b.size(); i++) {
                long p = b.get(i);
                if ((p - x) > 3500) {
                    count++;
                }
                x = b.get(i);
            }
//          Get the rate per minute
            int rate = (int) (((float) count / 45) * 60);
//          Return the heart rate
            return String.valueOf(rate / 2);
        }
    }

//  Method to measure resp rate
    private void measure_resp_rate() {

//      Respiratory rate toast before start
        Toast.makeText(getApplicationContext(), "Please place the phone on your chest for 45 seconds.", Toast.LENGTH_LONG).show();

//      If the sensor is not running
        if (!isSensorRunning) {
//          Register and start the accelerometer sensor
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

//          Start the timer
            timer.start();
        } else {
//          Stop the sensor
            stopSensor();
        }

//      Set the sensor running flag
        isSensorRunning = !isSensorRunning;
    }

//  Method for stopping the sensor
    public void stopSensor() {
//      Unregister the accelerometer sensor
        sensorManager.unregisterListener(sensorEventListener);
//      Stop the timer
        timer.cancel();
//      Calculate the respiration rate
        int resp_rate = calculate_resp_rate();
//      Set the respiration rate on text view on the screen
        tv_resp_rate_display.setText(String.valueOf(resp_rate));
    }

//  Method to calculate resp rate
    protected int calculate_resp_rate() {
        float previousValue = 0f;
        float currentValue = 0f;
        previousValue = 10f;
        int k = 0;

        for (int i = 11; i < Math.min(accelX.size(), Math.min(accelY.size(), accelZ.size())); i++) {
            currentValue = (float) Math.sqrt(
                              Math.pow(accelZ.get(i), 2.0)
                            + Math.pow(accelX.get(i), 2.0)
                            + Math.pow(accelY.get(i), 2.0) );
            if (Math.abs(previousValue - currentValue) > 0.15) {
                k++;
            }
            previousValue = currentValue;
        }

        double ret = k / 45.00;

//      Return the respiratory rate
        return (int) (ret * 30);
    }

}