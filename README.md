# AutoDispatch

**AutoDispatch** is a comprehensive transport management and automated dispatch system. It provides a robust backend API for managing logistics and a cross-platform mobile/desktop application for users, drivers, and administrators.

## 🚀 Project Overview

The project is split into two main components:
- **[AutoDispatchApi](./AutoDispatchApi)**: A high-performance Ktor-based backend with a PostgreSQL database.
- **[AutoDispatchApp](./AutoDispatchApp)**: A Kotlin Multiplatform (KMP) application targeting Android, iOS, and Desktop (JVM), built with Compose Multiplatform.

---

## 🛠 Tech Stack

### Backend (AutoDispatchApi)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Ktor](https://img.shields.io/badge/ktor-%23000000.svg?style=for-the-badge&logo=ktor&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)

- **Framework:** [Ktor](https://ktor.io/) 3.4.0 (Netty engine)
- **Database & ORM:** [PostgreSQL](https://www.postgresql.org/) with [Exposed](https://github.com/JetBrains/Exposed) ORM
- **Migrations:** [Liquibase](https://www.liquibase.org/)
- **DI:** [Koin](https://insert-koin.io/)
- **Messaging:** [RabbitMQ](https://www.rabbitmq.com/) (via Ktor RabbitMQ plugin)
- **Security:** JWT Authentication
- **Documentation:** Swagger UI & OpenAPI
- **Reports:** Apache POI (Excel) & OpenPDF (PDF generation)
- **Email:** Simple Java Mail

### Frontend (AutoDispatchApp)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-black?style=for-the-badge&logo=kotlin&logoColor=%237F52FF)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=ios&logoColor=white)
![Desktop](https://img.shields.io/badge/Desktop-JVM-blue?style=for-the-badge&logo=openjdk&logoColor=white)

- **Framework:** [Kotlin Multiplatform](https://www.jetbrains.com/lp/multiplatform/) (KMP)
- **UI:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **Networking:** Ktor Client (Content Negotiation, Logging, Auth)
- **DI:** Koin (with Compose & ViewModel support)
- **Navigation:** AndroidX Navigation (Multiplatform)
- **Storage:** DataStore (Preferences)
- **Images:** Coil 3
- **Coloring:** MaterialKolor (Dynamic Material 3 colors)

---

## 📂 Project Structure

```text
AutoDispatch/
├── AutoDispatchApi/           # Backend Ktor Server
│   ├── src/main/kotlin/       # Kotlin source code (Domain, Service, Repository, Route layers)
│   ├── src/main/resources/    # Configuration files (application.yaml, database migrations)
│   ├── docker-compose-*.yaml  # Docker deployment configurations
│   └── build.gradle.kts       # Backend build script
├── AutoDispatchApp/           # Multiplatform Client App
│   ├── composeApp/            # Shared UI and logic (commonMain, androidMain, iosMain, jvmMain, mobileMain)
│   ├── androidApp/            # Android-specific entry point
│   ├── iosApp/                # iOS-specific entry point (SwiftUI)
│   └── build.gradle.kts       # Multiplatform build script
└── README.md                  # This file
```

---

## 🏗 Key Domain Modules

- **Auth:** Secure user registration and login using JWT.
- **Request Management:** Creation and tracking of transport requests.
- **Delivery Workflow:** Core logic for automated dispatch and delivery status tracking.
- **Driver & Vehicle:** Management of driver profiles, vehicle assignments, and availability.
- **Documents:** Automated generation of invoices and reports in PDF and Excel formats.
- **Statistics:** Real-time monitoring and historical data analysis.
- **Notifications:** Integration with RabbitMQ for asynchronous task processing and notifications.

---

## 🚦 Getting Started

### Prerequisites
- **JDK 17+** (JDK 21 recommended)
- **Android SDK** (for Android App)
- **Xcode** (for iOS App, macOS only)
- **Docker & Docker Compose** (for Backend/Database)

### Running the Backend
1. Navigate to the API directory: `cd AutoDispatchApi`
2. Configure your environment in `src/main/resources/application.yaml` or set environment variables.
3. Run with Gradle: `.\gradlew.bat run` (Windows) or `./gradlew run` (Unix).
4. Access Swagger UI at `http://localhost:8084/swagger-ui`.

### Running the Client App
1. Navigate to the App directory: `cd AutoDispatchApp`
2. **Android:** `.\gradlew.bat :androidApp:assembleDebug`
3. **Desktop:** `.\gradlew.bat :composeApp:run`
4. **iOS:** Open `iosApp/iosApp.xcodeproj` in Xcode or use the KMP plugin in IntelliJ/Android Studio.

---

## 🐳 Deployment & Development

The backend is container-ready with a multi-stage `Dockerfile` and several Docker Compose configurations for different environments:

- **`docker-compose-debug.yaml`**: Optimized for local development and debugging.
- **`docker-compose-release.yaml`**: For production-like deployment with Nginx as a reverse proxy.

### Using Docker Compose
To start the entire stack in release mode (API, PostgreSQL, RabbitMQ, Nginx):
```bash
cd AutoDispatchApi
docker compose -f docker-compose-release.yaml up --build
```

To start for debugging:
```bash
cd AutoDispatchApi
docker compose -f docker-compose-debug.yaml up --build
```

### Manual Docker Build
To build only the API image:
```bash
cd AutoDispatchApi
docker build -t autodispatch-api .
```

---
