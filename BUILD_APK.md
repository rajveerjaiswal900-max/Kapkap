# Build the installable Nexora APK

This project folder is source code, not an APK yet. GitHub Actions builds it on a hosted runner.

## Build with GitHub Actions

1. Open the repository's **Actions** tab.
2. Select **Build Nexora Android APK**.
3. Choose **Run workflow** if it has not started automatically.
4. Open the completed workflow run and download **Nexora-debug-apk**.
5. Extract the artifact ZIP. The file inside is `app-debug.apk`. Transfer it to your Android device and install it.

The workflow builds a **debug-signed APK** suitable for testing. For public distribution, create a proper release signing key, keep it private, and configure secure signing separately.

## Build locally

Use Android Studio with JDK 17, Android SDK Platform 35, and Gradle 8.9. Sync the project and choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**. Output: `app/build/outputs/apk/debug/app-debug.apk`.

## Before use

- Configure a backend URL you own or are authorized to use in app Settings.
- The backend must accept OpenAI-compatible Chat Completions JSON and return `choices[0].message.content`.
- Avoid shipping production API keys in the app; keep provider secrets on a secure server.
- This starter project has not been compiled or device-tested in the authoring environment.
