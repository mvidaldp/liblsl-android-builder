# liblsl-android-builder
This Android App simple project builds __LSL (LabStreamingLayer)__ library `liblsl.so` for architectures `arm64-v8a`, `armeabi-v7a`, `x86`, and `x86_64` on Android Studio. A demonstration App that uses __LSL__ is also included on it (more details below).

When the build is ready, the library files are inside `liblsl-android-builder/libs/debug/` and `liblsl-android-builder/libs/release/`. To avoid building it, you can also [download them from the last release](https://github.com/mvidaldp/liblsl-android-builder/releases/latest).

To build it yourself and/or to know more about how to include it on your Android project, check the details under the [builder app module instructions file (`build.gradle`)](https://github.com/mvidaldp/liblsl-android-builder/blob/main/builder/build.gradle).

There it is also explained how to make the build directly using the compiled/build library `liblsl.so`. But in a nutshell, just extract the architecture folders either from your own build or from the [lastest release (liblsl-version)](https://github.com/mvidaldp/liblsl-android-builder/releases/latest) into the `jniLibs` folder (e.g. `app-root/module-root/src/main/jniLibs` for release build, `app-root/module-root/src/debug/jniLibs` for debug one). Also, do not include (or just comment) the `CMake` blocks from `build.gradle` (`externalNativeBuild` and `externalNativeBuild.cmake`) and it will work.

## Demo App (LSL Markers Sender Example)
<img src="https://github.com/mvidaldp/liblsl-android-builder/raw/main/media/app_screenshot.png" height="600">

This demo App sends the button label of the button pressed/clicked (`A` or `B`) as triggers or markers using a __LSL__ stream.
You can test the App by either building and running it on Android Studio or directly installing the [APK release](https://github.com/mvidaldp/liblsl-android-builder/releases/latest).

![keyboard-trigger](https://github.com/mvidaldp/liblsl-android-builder/raw/main/media/log_demo.gif)

The file where the actual __LSL__ API is used is on [MainActivity.java](https://github.com/mvidaldp/liblsl-android-builder/blob/main/builder/src/main/java/liblsl/android/builder/MainActivity.java). For more examples, check the [official community examples from the __LSL__ Java repo](https://github.com/labstreaminglayer/liblsl-Java/tree/master/src/examples).
