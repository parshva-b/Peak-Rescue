package com.example.com.peakrescue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.widget.EditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class SOS_Activity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 2;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        requestSMSPermission();
        requestAlarmPermissionOnly();
    }
    public void sendSOSMessage(View view) {
        // Find the EditText for phone number
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);

        // Get the phone number from the EditText
        String enteredPhoneNumber = phoneNumberEditText.getText().toString().trim();

        // If the entered number is empty or less than 10 digits, use the default number
        String emergencyPhoneNumber = (enteredPhoneNumber.length() >= 10) ? enteredPhoneNumber : "2722761518";

        // Check for and request permissions if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                // Permission is already granted
                sendSMS(emergencyPhoneNumber);
            }
        } else {
            // Permission is implicitly granted on devices below Marshmallow
            sendSMS(emergencyPhoneNumber);
        }
    }
    public void raiseAlarm(View view) {
        // Check for and request permissions if needed
        requestAlarmPermission();
    }

    private void requestSMSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    private void requestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            } else {
                // Permission is already granted
                ringAlarm();
            }
        } else {
            // Permission is implicitly granted on devices below Marshmallow
            ringAlarm();
        }
    }

    private void requestAlarmPermissionOnly() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }

    private void ringAlarm() {
        // Play the default notification sound
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        // Ring the alarm for 3 seconds
        r.play();

        // Stop the alarm after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                r.stop();
            }
        }, 3000);

        // Display a toast message
        Toast.makeText(getApplicationContext(), "Alert has been raised and contacted emergency services", Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(String phoneNumber) {
        try {
            String emergencyMessage = "Emergency! Please help!";

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, emergencyMessage, null, null);

            Toast.makeText(getApplicationContext(), "SOS Message Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to send SOS message", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void call911(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {
                // Permission is already granted
                confirmCall911();
            }
        } else {
            // Permission is implicitly granted on devices below Marshmallow
            confirmCall911();
        }
    }

    private void confirmCall911() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This is just a demo. Are you sure you want to contact 911?")
                .setTitle("Confirm 911 Call")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        make911Call(); // Proceed with the 911 call
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the operation, do nothing
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void make911Call() {
        // Replace this with the actual code to make a call to 911
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:911"));

        try {
            startActivity(dialIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Unable to initiate a call to 911", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with the SMS functionality or handle as needed
            } else {
                Toast.makeText(this, "Permission denied. Cannot send SOS message.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_RECEIVE_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with the alarm functionality or handle as needed
                ringAlarm();
            } else {
                Toast.makeText(this, "Permission denied. Cannot raise alarm.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the 911 call
                confirmCall911();
            } else {
                Toast.makeText(this, "Permission denied. Cannot call 911.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}