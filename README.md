# Nexora AI Agent

Original Android chat app project with Nexora branding, an animated Agent typing indicator, a responsive Jetpack Compose chat UI, and a configurable OpenAI-compatible API endpoint.

## Build an APK

This repository is configured to build a debug APK with GitHub Actions. Open the **Actions** tab, select **Build Nexora Android APK**, and run the workflow (or push a commit). When the run succeeds, open the run and download the `Nexora-debug-apk` artifact.

## Requirements

- JDK 17
- Gradle 8.9
- Android SDK platform 35 and build tools installed by the workflow

## Important

This is an original project, not a modified third-party APK. No payment UI or billing SDK is included. Configure your own authorized OpenAI-compatible endpoint/model in the app settings. Avoid putting production API keys in a client app; use a secure backend for real deployments.

See `BUILD_APK.md` for details.