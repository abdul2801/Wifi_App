
---

# IIIT Kottayam Auto-Login

This Android application is designed to automatically log in to the IIIT Kottayam network, allowing you to stay connected without manual intervention. The app runs continuously in the background, detecting the network and logging in whenever required.

## Features

- **Auto Login**: Automatically logs in to the IIIT Kottayam network when detected.
- **Background Operation**: Runs in the background, ensuring seamless connectivity.
- **Logcat Viewer**: Displays logs in real-time for monitoring the app's activity.

## Requirements

- **Location Access**: The app requires location access to function correctly, as Android uses location services to detect Wi-Fi networks.
- **Android 5.0+**: Compatible with devices running Android 5.0 (Lollipop) and above.

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/IIITKottayam-AutoLogin.git
```

### 2. Open in Android Studio
- Open the cloned project in Android Studio.

### 3. Build and Install
- Build the project and install it on your Android device.

## Usage

### 1. Configure Credentials
- Upon first run, enter your IIIT Kottayam network credentials (username and password).
- These credentials will be securely stored using Android's `SharedPreferences`.

### 2. Enable Location Services
- Ensure that location services are enabled on your device, as the app requires location access to detect the IIIT Kottayam network.

### 3. Background Operation
- The app runs in the background, automatically logging you in whenever the network is detected.
- You can view real-time logs of the app's activity in the Logcat viewer.


## Contributing

Contributions are welcome! Please submit a pull request or open an issue for any suggestions or improvements.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Feel free to customize this README with any additional details specific to your project!
