## Peak Rescue Component: Health and Environmental Data Collection
This repository contains the essential components responsible for collecting heart rate, respiratory rate, and altitude data within the context of an Acute Mountain Sickness (AMS) prediction application for trekking activities.

**Overview**
This component plays a crucial role in gathering critical health parameters and environmental data necessary for the assessment of AMS risk during high-altitude trekking. The components include modules for heart rate measurement utilizing the camera sensor, respiratory rate estimation using the accelerometer sensor, and altitude retrieval through GPS functionality.

**Features**
Heart Rate Measurement:

Utilizes the phone's camera sensor for capturing photoplethysmographic (PPG) data.
Algorithms for precise heart rate calculation based on captured PPG signals.

Respiratory Rate Estimation:

Leverages the accelerometer sensor to analyze chest movements for respiratory rate estimation.
Algorithms for interpreting accelerometer data to estimate respiratory rate patterns.

Altitude Retrieval from GPS:

Harnesses the GPS module to fetch accurate altitude information.
Algorithms for processing GPS data to determine the user's current altitude.

**Tech Stack**
Programming Languages: Java for Android development.
Sensor Integration: Android Sensor Framework, CameraManager, SensorManager.
GPS Integration: LocationManager.

**Usage**
Installation: Clone or download this repository.
Configuration: Ensure necessary permissions for camera, accelerometer, and GPS usage.
Integration: Integrate these components into your application.
Testing: Conduct rigorous testing across various scenarios for accuracy and reliability.

**Execution**
Installation: Clone or download the app_debug.apk on your android device.
Permission: Provide all the required permission or access.
Hear rate: Click on "Get Heart Rate" button and follow the instructions to obtain heart rate.
Respiratory rate: Click on "Get Respiratory Rate" button and follow the instructions to obtain heart rate.
Altitude: Click on "Get Altitude" button to obtain altitude through GPS.
