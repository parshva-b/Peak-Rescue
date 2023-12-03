# Oximeter Module

## Overview
This module is designed to simulate the reading of SpO2 levels of the user from an oximeter connected via a Bluetooth connection. Since we do not have access to the physical oximeter device, this module simulates a mock connection to an external device via a Bluetooth connection and calculates the SpO2 level (Blood oxygen saturation) within 5 seconds.

## Getting Started
To use this module, you will need to open this project in Android Studio to work and build the APK into an Android emaulator or a physical Android phone.

## Usage
1. Press on the "Turn On Bluetooth" button
    - Warning: Module will not work without a bluetooth feature available on the device/emulator
    - If Bluetooth is off, the app will ask you to enable Bluetooth
    - If Bluetooth is already on, it will display a message "Bluetooth is already enabled"
2. Press on the "Paird Devices" button to show all the Bluetooth device the phone has connected to.
3. Select any preferred device to setup a Bluetooth connection.
4. Bluetooth device has been successfully paired, and the "Calculate SpO2" is ready to be pressed to measure the SpO2 levels.
