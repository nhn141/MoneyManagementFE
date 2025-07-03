# MoneyManagementFE (Android)

This is the Android frontend for the MoneyManagement platform, built with Kotlin, Jetpack Compose, Hilt, and Retrofit. It connects to the [MoneyManagement-Server backend](https://github.com/fishperson113/MoneyManagement-Server) to provide a modern, mobile-first experience for financial management, social features, and real-time collaboration.

---

## 🚀 Prerequisites

-   Android Studio (Giraffe or newer recommended)
-   Android SDK 33+
-   [MoneyManagement-Server backend](https://github.com/fishperson113/MoneyManagement-Server) running (see backend README for setup)

---

## 🛠️ How to Run

1. **Clone this repository:**

    ```sh
    git clone https://github.com/your-username/MoneyManagementFE.git
    cd MoneyManagementFE
    ```

2. **Open in Android Studio:**

    - Open the project folder in Android Studio.

3. **Configure API Endpoint (Optional):**

    - The API base URL is set in `NetworkModule.kt`:
        ```kotlin
        private const val BASE_URL = "http://143.198.208.227:5000/api/"
        ```
    - Change this if you want to use a different backend server (e.g., local or emulator).

4. **Build and Run:**
    - Select an emulator or connect a device.
    - Click **Run** (▶️) in Android Studio.

---

## 📂 Project Overview

### Core Features

-   **Financial Management:** Wallets, transactions, categories, and budgeting
-   **Social Features:** Friend system, group chats, news feed, and posts
-   **Reporting:** View and download financial reports
-   **Real-time Communication:** Chat and notifications
-   **Authentication:** Secure login and registration

### Tech Stack

-   **Kotlin** with Jetpack Compose for UI
-   **Hilt** for dependency injection
-   **Retrofit** and **OkHttp** for networking
-   **Gson** for JSON parsing
-   **ViewModel** and **LiveData/StateFlow** for state management
-   **Material Design**

---

## 🏗️ Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── API/                # Retrofit API interfaces and interceptors
│   │   │   ├── DI/                 # Hilt modules (see NetworkModule.kt)
│   │   │   ├── ui/                 # Jetpack Compose UI components
│   │   │   ├── viewmodel/          # ViewModels for screens
│   │   │   └── ...
│   │   └── res/                    # Resources (themes, strings, etc.)
│   └── ...
├── build.gradle.kts
└── ...
```

---

## 🔑 API Integration

-   Uses Retrofit with OkHttp for HTTP requests.
-   `NetworkModule.kt` configures:
    -   **Base URL** for backend API
    -   **LanguageInterceptor** for localization
    -   **AuthInterceptor** for JWT authentication
    -   **HttpLoggingInterceptor** for debugging
    -   (Current setup trusts all SSL certificates for development; update for production!)

---

## ✅ Useful URLs

### Backend API Types

-   **Emulator (Android Studio):**
    -   `http://10.0.2.2:5000` (maps to localhost of your development machine)
-   **Local Network (IPv4):**
    -   `http://<your-local-ip>:5000` (replace `<your-local-ip>` with your computer's IPv4 address, e.g., `http://192.168.1.14:5000`)
-   **Production/Deployed Server:**

    -   `http://143.198.208.227:5000` (public server)

-   **Swagger UI:**
    -   Emulator/Local: `http://10.0.2.2:5000/swagger` or `http://<your-local-ip>:5000/swagger`
    -   Production: [http://143.198.208.227:5000/swagger](http://143.198.208.227:5000/swagger)

---

## 🧪 Testing

-   Unit and UI tests can be run from Android Studio (**Run > Run Tests**).

---

## 🔒 Security Notes

-   The current OkHttp client in `NetworkModule.kt` trusts all SSL certificates (for development only). For production, use proper certificate pinning and validation.
-   JWT tokens are handled via `AuthInterceptor`.

---

## 🚀 Deployment

-   Build a release APK or App Bundle from Android Studio (**Build > Build Bundle(s) / APK(s)**).
-   Distribute via Google Play or other channels.

---

## 🤝 Contributing

Contributions are welcome! Please open issues and pull requests as needed.

---

## 📄 License

This project is licensed under the MIT License.
