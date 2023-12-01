plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.project4weatherfuzzylogicandsosservice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.project4weatherfuzzylogicandsosservice"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.multidex:multidex:2.0.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation(files("libs/jFuzzyLogic.jar"))
//    implementation(files("libs/jFuzzyLogic_2.0.7.jar"))
//    implementation(files("libs/jFuzzyLogic.jar"))
//    implementation(files("libs/jFuzzyLogic_2.0.7.jar"))
//    implementation(files("libs/jFuzzyLogic_2.0.7.jar"))
//    implementation(files("libs/jFuzzyLogic.jar"))
//    implementation(files("libs/jFuzzyLogic.jar"))
//    implementation(files("libs/jFuzzyLogic.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.core:core-ktx:1.12.0")
//    implementation(files("libs/jFuzzyLogic_2.0.7.jar"))
//    implementation("net.sourceforge.jFuzzyLogic:jFuzzyLogic:2.0.7")

}