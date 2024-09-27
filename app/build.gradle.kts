plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.rhewum"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rhewum"
        minSdk = 29
        targetSdk = 34
        versionCode = 2
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
    buildFeatures{
        viewBinding=true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation (libs.graphview)
    implementation(libs.constraintlayout)
    implementation (libs.calligraphy3)
    implementation (libs.startup.runtime)

    implementation (libs.emoji2)
    implementation (libs.lifecycle.runtime.ktx)
    implementation (libs.ormlite.android)
    implementation (libs.opencsv)

    // barchart
    implementation (libs.mpandroidchart)
    implementation (libs.jtransforms)

    // :::::::: Retrofit ::::
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    //slider
    implementation (libs.autoimageslider)
    //glide
    implementation(libs.glide)

    //firebase
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(group = "com.itextpdf", name = "itextpdf", version = "5.5.13.1")
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)
    implementation (libs.shortcutbadger)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}