# Nexora AI Agent

> A dark, neon-inspired Android AI chat starter built with Kotlin and Jetpack Compose.

**Nexora** is an original Android app project focused on a clean chat experience, an animated **Agent is responding** indicator, and a configurable connection to an authorized OpenAI-compatible API backend.

[![Build Nexora Android APK](https://github.com/rajveerjaiswal900-max/Kapkap/actions/workflows/build-apk.yml/badge.svg)](https://github.com/rajveerjaiswal900-max/Kapkap/actions/workflows/build-apk.yml)

## Features

- Nexora-branded dark UI with mint/cyan accents
- Responsive chat layout built with Jetpack Compose
- Clear **AGENT** response labels and animated typing dots
- Settings for backend URL, model name, and optional bearer token
- OpenAI-compatible Chat Completions request/response format
- GitHub Actions workflow for a downloadable debug APK
- No in-app payment screen or billing SDK in this starter

## Project status

This is an early starter project. The CI workflow is being tested; check the [Actions page](https://github.com/rajveerjaiswal900-max/Kapkap/actions) for the latest build result. The app has not yet been verified on a physical Android device.

## Build an APK

1. Open [Actions → Build Nexora Android APK](https://github.com/rajveerjaiswal900-max/Kapkap/actions/workflows/build-apk.yml).
2. Open the latest successful run.
3. Download the `Nexora-debug-apk` artifact.
4. Extract the ZIP; it contains `app-debug.apk`.

The workflow uses JDK 17, Gradle 8.9, Android SDK Platform 35, and produces a debug-signed APK for testing.

See [BUILD_APK.md](BUILD_APK.md) for local build steps and troubleshooting.

## Configure the backend

Launch the app and open **Settings**. Set:
- **Chat completions URL** — an HTTPS endpoint you own or are authorized to use
- **Model name** — the model identifier expected by your backend
- **API key** — optional bearer token, if your backend requires it

The endpoint is expected to accept an OpenAI-compatible request containing `model` and `messages`, then return a response with `choices[0].message.content`.

### Security note

Do not ship production provider secrets inside a mobile app. Client-side keys can be extracted. For a real deployment, put the provider key on a secure backend you control and have the app call that backend over HTTPS.

## Tech stack

- Kotlin
- Android Jetpack Compose / Material 3
- OkHttp
- Gradle Kotlin DSL
- GitHub Actions

## Repository layout

```
.
├── .github/workflows/build-apk.yml
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/nexora/agent/MainActivity.kt
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── BUILD_APK.md
```

## Contributing

Bug reports and focused improvements are welcome. Please test builds and avoid committing API keys, signing keys, or personal tokens. See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

No open-source license has been added yet. Until a license is chosen, public visibility does not automatically grant permission to reuse, modify, or redistribute this code.
