package com.example.com.peakrescue;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;
import java.util.Set;


public class OxymeterActivity extends AppCompatActivity {

    private Button connectBtButton, showDevicesButton, calcOxiButton;
    private TextView spo2Text;
    private ListView deviceList;
    private BluetoothAdapter btAdapter;

    private static final int REQUEST_ENABLE_BT = 2;
    private static final String BLUETOOTH_PERMISSION = Manifest.permission.BLUETOOTH;
    private boolean isBluetoothConnected = false;

    private Button bt_nextButton;
    private String formattedSpo2Value;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxymeter);

        connectBtButton = findViewById(R.id.btToggleBtn);
        showDevicesButton = findViewById(R.id.showBTDevice);
        deviceList = findViewById(R.id.devicesListView);
        spo2Text = findViewById(R.id.spo2Text);
        calcOxiButton = findViewById(R.id.calcSpo2);

        bt_nextButton = findViewById(R.id.nextButton);

        connectBtButton.setOnClickListener(view -> toggleBluetooth());
        showDevicesButton.setOnClickListener(view -> showPairedBTDevices());
        calcOxiButton.setOnClickListener(view -> {
            try {
                calculateSpo2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        bt_nextButton.setOnClickListener(view -> {
            try {
                nextActivity();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth enabling cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void toggleBluetooth() {
        if (ContextCompat.checkSelfPermission(OxymeterActivity.this, BLUETOOTH_PERMISSION) == PackageManager.PERMISSION_DENIED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(OxymeterActivity.this, new String[]{BLUETOOTH_PERMISSION}, REQUEST_ENABLE_BT);
        } else {
            // Permission granted, toggle Bluetooth
            BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (Build.VERSION.SDK_INT >= 31) {
                btAdapter = btManager.getAdapter();
            } else {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
            }

            if (btAdapter == null) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Bluetooth is not supported", Toast.LENGTH_LONG).show());
                return;
            }

            if (!btAdapter.isEnabled()) {

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, BLUETOOTH_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_PERMISSION}, REQUEST_ENABLE_BT);
                    return;
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show());
            }
        }

    }

    private void showPairedBTDevices() {
        if (btAdapter != null) {
            // Check and request Bluetooth permission if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                return;
            }

            Set<BluetoothDevice> bt = btAdapter.getBondedDevices();
            BluetoothDevice[] devices = bt.toArray(new BluetoothDevice[0]);

            if (devices.length > 0) {
                String[] deviceNames = new String[devices.length];
                int index = 0;

                for (BluetoothDevice device : devices) {
                    deviceNames[index] = device.getName();
                    index++;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = view.findViewById(android.R.id.text1);

                        int textColor = getResources().getColor(
                                AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
                                        ? android.R.color.white
                                        : android.R.color.black);

                        textView.setTextColor(textColor);
                        return view;
                    }
                };

                deviceList.setAdapter(arrayAdapter);

                deviceList.setOnItemClickListener((parent, view, position, id) -> {
                    BluetoothDevice selectedDevice = devices[position];
                    connectToDevice(selectedDevice);
                });
            } else {
                Toast.makeText(getApplicationContext(), "No devices found, please turn on Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to simulate connecting to the selected Bluetooth device
    private void connectToDevice(BluetoothDevice device) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Toast.makeText(getApplicationContext(), "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();

            // Set Bluetooth connected flag to true
            isBluetoothConnected = true;
        }, 2000);
    }

    // Method to calculate Spo2 and store it in the database
    private void calculateSpo2() throws InterruptedException {
        // Check if a Bluetooth device is connected
        if (!isBluetoothConnected) {
            // Display Toast if no Bluetooth device is connected
            Toast.makeText(getApplicationContext(), "Connect to a Bluetooth device", Toast.LENGTH_SHORT).show();
            return;
        }

        spo2Text.setText("SpO2: Calculating...");

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Simulate Spo2 calculation
            Random random = new Random();
            double spo2Value = random.nextGaussian() * 2 + 95;
//            spo2Value = Math.max(88, Math.min(spo2Value, 100));
            spo2Value = 95;
            // Store the Spo2 value in the database
//            saveSpo2ToDatabase(spo2Value);
            formattedSpo2Value = String.format("%.2f", spo2Value);
            runOnUiThread(() -> spo2Text.setText("SpO2: " + formattedSpo2Value + "%"));
        }, 5000);
        nextActivity();
    }

    private void nextActivity() throws InterruptedException {
        spo2Text.setText("SPO2: 95");
        formattedSpo2Value = String.valueOf(95);
        Handler handler = new Handler();
        handler.postDelayed(()->{
            setSharedPrefData();
            Intent i = new Intent(this, UserActivity.class);
            startActivity(i);
        }, 3000);

    }

    private void setSharedPrefData() {
        SharedPreferences sharedPreferences = getSharedPreferences("allData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve existing weather data as a JSON string
        String allDataString = sharedPreferences.getString("allData", "{}");

        try {
            // Convert the JSON string to a JSON object
            JSONObject allData = new JSONObject(allDataString);

            // Update the attribute with the new value
            allData.put("spo2", formattedSpo2Value);

            // Save the updated JSON object as a string in SharedPreferences
            editor.putString("allData", allData.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}