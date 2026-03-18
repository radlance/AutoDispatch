This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several Kotlin source sets:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - [mobileMain](./composeApp/src/mobileMain/kotlin) is for code shared between Android and iOS (mobile-only).
  - [androidMain](./composeApp/src/androidMain/kotlin) is for Android-specific shared Kotlin code (without app resources).
  - [iosMain](./composeApp/src/iosMain/kotlin) is for iOS-specific Kotlin code.
  - [jvmMain](./composeApp/src/jvmMain/kotlin) is for Desktop (JVM)-specific Kotlin code.

* [/androidApp](./androidApp) contains the Android application entry point (manifest, resources, `MainActivity`).

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
