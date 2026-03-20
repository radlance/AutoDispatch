import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    applyDefaultHierarchyTemplate()

    android {
        namespace = "com.github.radlance.autodispatch.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        androidResources { enable = true }
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
            implementation(libs.androidx.exifinterface)
            implementation(libs.play.services.location)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(libs.ui.backhandler)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.zoomable)
            implementation(libs.kotlinx.datetime)
            implementation(libs.slf4j.simple)
            implementation(libs.atomicfu)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.datastore.preferences)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.bundles.ktor)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
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
            implementation(libs.ui.backhandler)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.jmapviewer)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.radlance.autodispatch.core.MainKt"

        nativeDistributions {
            modules("jdk.unsupported")
            val iconsDir = project.file("src/jvmMain/resources/icons")

            macOS {
                iconFile.set(iconsDir.resolve("icon.icns"))
            }
            windows {
                iconFile.set(iconsDir.resolve("icon.ico"))
            }
            linux {
                iconFile.set(iconsDir.resolve("icon.png"))
            }
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AutoDispatch"
            packageVersion = "1.0.0"
        }
    }
}
