plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.head"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.head"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    packagingOptions {
        // 중복된 META-INF/DEPENDENCIES 파일을 무시합니다.
        exclude("META-INF/DEPENDENCIES")
        // 필요에 따라 추가로 무시할 파일을 지정할 수 있습니다.
        exclude("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Google API GAX (required by the Speech API)
    implementation("com.squareup.retrofit2:converter-gson:2.3.0")
    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")

    implementation("com.google.android.gms:play-services-tasks:18.2.0")
    implementation("com.google.api:gax:2.0.0")

    // Firebase 의존성
    implementation("com.google.firebase:firebase-auth:21.1.0")
    implementation("com.google.firebase:firebase-firestore:24.4.0")
    implementation("com.google.firebase:firebase-storage:20.1.0")
    implementation(libs.volley)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
