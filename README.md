## MAD Assignment — Mobile Application Development (CSE3709)

## Q1 — Currency Converter App


A currency converter app that converts between INR, USD, JPY and EUR. Also added a Settings page to switch between Light and Dark theme which stays saved after closing the app.
What I Used

## Java, XML, ArrayAdapter, SharedPreferences, AppCompatDelegate, CardView

## How It Works

User enters amount, selects From and To currency, presses Convert and result shows instantly. Swap button reverses the currencies. Dark mode toggle in Settings saves the preference permanently.

## Problems & Solutions

Spinner blank — forgot to attach adapter to both spinners
Theme resetting — moved applyThemeFromPrefs() to before super.onCreate()
Too many decimals — used String.format("%.4f") and "%.0f" for JPY
Plain UI — added CardView and Material button styles

## Conclusion

Learned how Spinners, SharedPreferences and runtime theme switching work in Android.

## Q2 — Media Player App

A media player that plays audio from device storage and streams video from a URL. Has all six buttons — Open File, Open URL, Play, Pause, Stop, Restart.
What I Used

## Java, XML, MediaPlayer, VideoView, MediaController, SeekBar, Handler, AlertDialog

## How It Works

Open File picks audio from storage and plays it with a moving SeekBar. Open URL shows a dialog to enter a video link which streams in VideoView. All six buttons work for both audio and video.
Problems & Solutions

Crash after Stop then Play — had to call prepare() before start() again
Video not loading — missing INTERNET permission and usesCleartextTraffic flag
SeekBar not moving — used Handler with Runnable updating every 500ms
File picker showing all files — changed intent type to "audio/*"
Crash when Play pressed with no media — added null check with Toast message

## Conclusion

Learned MediaPlayer lifecycle properly and how VideoView handles streaming automatically.

## Q3 — Sensor Data App

An app that reads and displays live data from three sensors — Accelerometer, Light and Proximity. Values update in real time on screen.
What I Used

Java, XML, SensorManager, SensorEventListener, CardView

## How It Works

App registers listeners on resume and unregisters on pause to save battery. onSensorChanged() updates the UI automatically. Accelerometer shows X Y Z in m/s², Light shows lux with description, Proximity shows near or far.
Problems & Solutions

Battery drain — moved register/unregister to onResume() and onPause()
Proximity only showing 0 or 5 — normal hardware behavior, handled with near/far labels
Light sensor not available on Samsung phone — manufacturer restriction, handled with null check and "Not available" message
Values flickering — changed to SENSOR_DELAY_UI for readable update speed

## Conclusion

Learned that sensors vary across devices and managing listeners with lifecycle is very important.

## Q4 — Photo Gallery App

A complete photo gallery app with three features — take photos with camera and save to folder, view all images in a folder as a grid, and tap any image to see details and delete it with a confirmation dialog.
What I Used

Java, XML, 3 Activities, Camera Intent, FileProvider, GridView, custom BaseAdapter, BitmapFactory, AlertDialog, Runtime Permissions, MediaScanner

## How It Works

Take Photo opens camera via FileProvider URI and saves to Pictures/PhotoGalleryApp/. Gallery shows images in 3 column grid with folder picker option. Detail screen shows name, path, size, date and a Delete button with confirmation dialog. After delete gallery reloads automatically.
Problems & Solutions

FileUriExposedException — had to use FileProvider instead of direct file URI
res/xml folder missing — created manually via New Android Resource Directory
Photo not in system gallery — sent ACTION_MEDIA_SCANNER_SCAN_FILE broadcast
OutOfMemoryError in grid — used BitmapFactory inSampleSize for downsampled thumbnails
Grid not refreshing after delete — called loadImagesFromFolder() in onResume()
Permissions broken on Android 13 — added READ_MEDIA_IMAGES with SDK version check
Wrong images in grid on scroll — always set image in every getView() call

## Conclusion

Most challenging project of the assignment. Learned FileProvider, bitmap memory management, multi activity communication and Android version differences in permissions.

Author: Roushan Kumar Singh
