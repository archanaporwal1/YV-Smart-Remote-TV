# YV Smart Remote - Universal Remote for Android TV & Google TV

An authentic, tactile Android TV and Google TV remote control application built with modern Jetpack Compose and Material Design 3.

---

## 📱 Download the APK Directly

You can download the ready-to-install Android APK file directly from this repository:

👉 **[Download YV-Smart-Remote.apk](apk/YV-Smart-Remote.apk?raw=true)** *(Direct APK file)*

### How to Install on Android:
1. Tap the download link above (or navigate to the `apk/` directory in this repo and click **Download**).
2. Open the downloaded `YV-Smart-Remote.apk` file on your Android phone.
3. If prompted, allow **"Install unknown apps"** from your browser/file manager.
4. Launch **YV Smart Remote** and connect to your smart TV on the same Wi-Fi network!

---

## 🚀 Features

- **Tactile D-Pad Controller**: Smooth 4-way navigation ring with center OK button and radial feedback.
- **Precision Touchpad**: Swipe gesture surface with tap-to-select and quick list scrollbar.
- **TV Apps Launcher**: One-tap quick launcher for YouTube, Netflix, Prime Video, Disney+, Spotify, Twitch, and more.
- **Channel Numpad**: Direct channel dialing with sub-channel dash (`-`) and TV Guide key.
- **Instant TV Keyboard**: Sync phone typing directly to TV search boxes and text inputs.
- **Hardware Rockers**: Tactile dual rockers for Volume (`+`, `-`, `Mute`) and Channels (`CH +`, `CH -`).
- **Google Assistant Voice Search**: Pulsating speech animation and instant voice recognition.
- **Smart TV Companion Bar**: Real-time mirroring of TV power, volume percentage, active HDMI input, and command toast feedback.
- **Zero Configuration & Manual IP Support**: Automatic mDNS discovery plus direct IP/port entry.

---

## 🛠️ Building from Source

To build the APK locally on your machine:

```bash
# Clone the repository
git clone <YOUR_GITHUB_REPO_URL>
cd <REPO_NAME>

# Build the debug APK (Mac/Linux)
./gradlew assembleDebug

# Build the debug APK (Windows)
gradlew.bat assembleDebug
```

The output APK will be placed in:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚙️ Automated CI/CD (GitHub Actions)

This repository includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`) that automatically builds the latest APK on every commit and pull request. You can also download fresh APK builds from the **Actions** tab.
