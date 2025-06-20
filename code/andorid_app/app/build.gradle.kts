plugins {
  id("com.android.application")
  id("com.google.gms.google-services")
}

android {
  namespace = "com.example.myapplication"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.myapplication"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {

  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.2.1")
  implementation("com.google.android.gms:play-services-location:21.0.1")


  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

  implementation("com.github.bumptech.glide:glide:4.13.0")
  implementation("androidx.navigation:navigation-fragment:2.8.9")
  implementation("androidx.navigation:navigation-ui:2.8.9")
  implementation("androidx.activity:activity-ktx:1.9.0")



  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  androidTestImplementation("androidx.test:rules:1.5.0")

  implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
  implementation("com.google.firebase:firebase-analytics")
  implementation("com.google.firebase:firebase-auth")
  implementation("com.google.android.gms:play-services-auth:21.0.0")
  implementation("com.google.firebase:firebase-firestore:24.10.3")
  implementation("com.google.firebase:firebase-storage:20.2.1")
  implementation("com.google.firebase:firebase-messaging:20.2.1")


  implementation("org.osmdroid:osmdroid-android:6.1.16")

  implementation("org.tensorflow:tensorflow-lite:2.13.0")
  implementation("org.tensorflow:tensorflow-lite-support:0.4.3")
  implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0")


  implementation("de.hdodenhof:circleimageview:3.1.0")

}
