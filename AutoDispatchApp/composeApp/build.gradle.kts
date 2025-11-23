import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()

    sourceSets {
        val mobileMain by creating {
            dependsOn(commonMain.get())
        }

        androidMain.get().dependsOn(mobileMain)
        iosMain.get().dependsOn(mobileMain)

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            api(libs.slf4j.simple)
            implementation(libs.atomicfu)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.datastore.preferences)
            api(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.bundles.ktor)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.material.kolor)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            val javafxVersion = libs.versions.javafx.get()
            val os = org.gradle.internal.os.OperatingSystem.current()

            val platform = when {
                os.isWindows -> "win"
                os.isMacOsX -> "mac"
                os.isLinux -> "linux"
                else -> error("Unsupported OS")
            }

            implementation("org.openjfx:javafx-base:$javafxVersion:$platform")
            implementation("org.openjfx:javafx-controls:$javafxVersion:$platform")
            implementation("org.openjfx:javafx-graphics:$javafxVersion:$platform")
            implementation("org.openjfx:javafx-fxml:$javafxVersion:$platform")
            implementation("org.openjfx:javafx-web:$javafxVersion:$platform")
            implementation("org.openjfx:javafx-swing:$javafxVersion:$platform")
            implementation(libs.datatable.material3)
            implementation(libs.ktor.client.okhttp)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.jmapviewer)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.github.radlance.autodispatch"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.github.radlance.autodispatch"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.github.radlance.autodispatch.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.github.radlance.autodispatch"
            packageVersion = "1.0.0"
        }
    }
}
