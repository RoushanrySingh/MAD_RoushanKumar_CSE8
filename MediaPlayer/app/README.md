# Media Player App

This is a simple Media Player Android application that I developed as part of my Mobile Application Development (MAD) assignment.

The main objective of this project was to understand how media playback works in Android. In this app, I implemented basic functionality where the user can play audio files from the device or stream video using a URL. I also added playback controls like Play, Pause, Stop, and Restart.

---

## Features

- Open and play audio files from device storage
- Stream video using a URL
- Playback controls:
    - Play
    - Pause
    - Stop
    - Restart
- Simple and clean user interface
- Easy to use for beginners

---

## Screenshots

### Layout Design (Initial UI)
![Layout](screenshots/layout.png)

### Final Output (Working App)
![Output](screenshots/output.png)

---

## How the App Works

When the app starts, the user sees two main options:

- **Open File** → Used to select an audio file from the device
- **Open URL** → Used to stream a video from the internet

After selecting the media, the user can control playback using the buttons provided:

- Play → Starts media
- Pause → Pauses media
- Stop → Stops media
- Restart → Plays media again from beginning

---

## Technologies Used

- Java (Android)
- XML (UI Design)
- Android Studio
- MediaPlayer API
- VideoView
- Git & GitHub

---

## Implementation Details

In this project:

- I used the **MediaPlayer class** for audio playback
- I used **VideoView** for video streaming
- Buttons were connected using **OnClickListener**
- Media resources were properly handled using release methods

## Example:
-```java
mediaPlayer.stop();
mediaPlayer.release();


Problems Faced & How I Solved Them

## 1. Media not playing initially

At first, the audio was not playing.
After checking the code, I realized that the data source was not set properly and MediaPlayer was not prepared correctly.
After fixing initialization and preparing MediaPlayer, it worked.

2. App crashing when clicking Stop

Whenever I clicked the Stop button, the app crashed.
Then I understood that MediaPlayer resources must be released properly.

I fixed it using:

mediaPlayer.stop();
mediaPlayer.release();
3. Buttons were not working

Some buttons were not responding initially.
The issue was that I had not set proper click listeners.

After adding correct OnClickListener for each button, the problem was solved.

4. Video streaming not working

When I tried streaming video using URL, it was not loading.
Then I implemented VideoView properly using URI, and the video started streaming.

5. UI alignment issues

Initially, the UI looked unorganized.
I fixed it by:

Adding proper padding and margins
Grouping controls
Improving layout design
## Project Structure

MediaPlayer/
├── app/
├── screenshots/
│   ├── layout.png
│   └── output.png
├── README.md


## Conclusion

This project helped me understand how media playback works in Android. I learned how to use MediaPlayer and VideoView, handle user interaction, and manage resources properly to avoid crashes. It also improved my understanding of UI design and debugging in Android applications.

## Author

Roushan Kumar Singh
BTech CSE