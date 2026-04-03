package com.example.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

/**
 * MainActivity - Media Player Activity
 *
 * Supports:
 *   a) Playing an audio file picked from device storage (MediaPlayer)
 *   b) Streaming a video from a URL (VideoView + MediaController)
 *
 * Buttons available: Open File, Open URL, Play, Pause, Stop, Restart
 */
public class MainActivity extends AppCompatActivity {

    // ── Request code for file picker ──
    private static final int PICK_AUDIO_REQUEST = 1;

    // ── Audio player ──
    private android.media.MediaPlayer mediaPlayer;
    private Uri audioUri;

    // ── UI components ──
    private VideoView videoView;
    private MediaController mediaController;

    private Button btnOpenFile, btnOpenUrl, btnPlay, btnPause, btnStop, btnRestart;
    private TextView tvStatus, tvNowPlaying;
    private SeekBar seekBar;

    // Seek bar update handler
    private Handler handler = new Handler();

    // Tracks which mode is active: "audio" or "video"
    private String currentMode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Media Player");
        }

        // Bind views
        videoView    = findViewById(R.id.videoView);
        btnOpenFile  = findViewById(R.id.btnOpenFile);
        btnOpenUrl   = findViewById(R.id.btnOpenUrl);
        btnPlay      = findViewById(R.id.btnPlay);
        btnPause     = findViewById(R.id.btnPause);
        btnStop      = findViewById(R.id.btnStop);
        btnRestart   = findViewById(R.id.btnRestart);
        tvStatus     = findViewById(R.id.tvStatus);
        tvNowPlaying = findViewById(R.id.tvNowPlaying);
        seekBar      = findViewById(R.id.seekBar);

        // Attach MediaController to VideoView (gives built-in controls too)
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // ── Button Listeners ──

        // Open local audio file via file picker
        btnOpenFile.setOnClickListener(v -> openAudioFile());

        // Open video from URL via dialog input
        btnOpenUrl.setOnClickListener(v -> showUrlDialog());

        // Play
        btnPlay.setOnClickListener(v -> playMedia());

        // Pause
        btnPause.setOnClickListener(v -> pauseMedia());

        // Stop
        btnStop.setOnClickListener(v -> stopMedia());

        // Restart
        btnRestart.setOnClickListener(v -> restartMedia());

        // SeekBar drag listener (only works for audio)
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Default state
        setPlaybackButtonsEnabled(false);
        tvStatus.setText("No media loaded");
    }

    // ────────────────────────────────────────────
    //  OPEN AUDIO FILE (from device storage)
    // ────────────────────────────────────────────

    /**
     * Launches the system file picker filtered to audio files.
     */
    private void openAudioFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST);
    }

    /**
     * Receives the selected audio file URI from the file picker.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            audioUri = data.getData();
            currentMode = "audio";

            // Hide video view, show seek bar for audio
            videoView.setVisibility(View.GONE);
            seekBar.setVisibility(View.VISIBLE);

            // Release any previous player
            releaseMediaPlayer();

            // Prepare new MediaPlayer
            mediaPlayer = new android.media.MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepareAsync(); // async so UI doesn't freeze

                tvStatus.setText("Preparing audio...");
                tvNowPlaying.setText("🎵 " + getFileNameFromUri(audioUri));

                mediaPlayer.setOnPreparedListener(mp -> {
                    tvStatus.setText("Audio ready — Press Play");
                    seekBar.setMax(mp.getDuration());
                    setPlaybackButtonsEnabled(true);
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    tvStatus.setText("Playback finished");
                    stopSeekBarUpdate();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    tvStatus.setText("Error playing audio");
                    return true;
                });

            } catch (IOException e) {
                Toast.makeText(this, "Cannot open file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // ────────────────────────────────────────────
    //  OPEN VIDEO FROM URL
    // ────────────────────────────────────────────

    /**
     * Shows an AlertDialog with an EditText for the user to enter a video URL.
     */
    private void showUrlDialog() {
        // Pre-fill with a sample MP4 URL for easy testing
        final EditText input = new EditText(this);
        input.setHint("https://example.com/video.mp4");
        input.setText("https://www.w3schools.com/html/mov_bbb.mp4");
        input.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(this)
                .setTitle("Enter Video URL")
                .setMessage("Paste a direct .mp4 / .m3u8 link:")
                .setView(input)
                .setPositiveButton("Load", (dialog, which) -> {
                    String url = input.getText().toString().trim();
                    if (url.isEmpty()) {
                        Toast.makeText(this, "URL cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loadVideoUrl(url);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Loads the given URL into the VideoView and prepares for streaming.
     */
    private void loadVideoUrl(String url) {
        currentMode = "video";

        // Release audio player if active
        releaseMediaPlayer();

        // Show VideoView, hide audio seekbar
        videoView.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.GONE);

        tvStatus.setText("Loading video...");
        tvNowPlaying.setText("🎬 Streaming video");

        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();

        videoView.setOnPreparedListener(mp -> {
            tvStatus.setText("Video ready — Press Play");
            setPlaybackButtonsEnabled(true);
        });

        videoView.setOnCompletionListener(mp -> tvStatus.setText("Video finished"));

        videoView.setOnErrorListener((mp, what, extra) -> {
            tvStatus.setText("Error loading video. Check URL.");
            Toast.makeText(this, "Cannot stream video. Check URL & internet.", Toast.LENGTH_LONG).show();
            return true;
        });
    }

    // ────────────────────────────────────────────
    //  PLAYBACK CONTROLS
    // ────────────────────────────────────────────

    private void playMedia() {
        if (currentMode.equals("audio")) {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                tvStatus.setText("▶ Playing audio");
                startSeekBarUpdate();
            }
        } else if (currentMode.equals("video")) {
            if (!videoView.isPlaying()) {
                videoView.start();
                tvStatus.setText("▶ Streaming video");
            }
        } else {
            Toast.makeText(this, "No media loaded. Use Open File or Open URL.", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMedia() {
        if (currentMode.equals("audio")) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                tvStatus.setText("⏸ Paused");
                stopSeekBarUpdate();
            }
        } else if (currentMode.equals("video")) {
            if (videoView.isPlaying()) {
                videoView.pause();
                tvStatus.setText("⏸ Paused");
            }
        }
    }

    private void stopMedia() {
        if (currentMode.equals("audio")) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                try {
                    // Re-prepare so it can be played again
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
                tvStatus.setText("⏹ Stopped");
                stopSeekBarUpdate();
            }
        } else if (currentMode.equals("video")) {
            videoView.stopPlayback();
            tvStatus.setText("⏹ Stopped");
        }
    }

    private void restartMedia() {
        if (currentMode.equals("audio")) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                tvStatus.setText("🔁 Restarted");
                startSeekBarUpdate();
            }
        } else if (currentMode.equals("video")) {
            videoView.seekTo(0);
            videoView.start();
            tvStatus.setText("🔁 Restarted");
        }
    }

    // ────────────────────────────────────────────
    //  SEEK BAR UPDATER (for audio only)
    // ────────────────────────────────────────────

    private Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }
    };

    private void startSeekBarUpdate() {
        handler.post(seekBarRunnable);
    }

    private void stopSeekBarUpdate() {
        handler.removeCallbacks(seekBarRunnable);
    }

    // ────────────────────────────────────────────
    //  HELPERS
    // ────────────────────────────────────────────

    /**
     * Enables or disables the Play/Pause/Stop/Restart buttons.
     */
    private void setPlaybackButtonsEnabled(boolean enabled) {
        btnPlay.setEnabled(enabled);
        btnPause.setEnabled(enabled);
        btnStop.setEnabled(enabled);
        btnRestart.setEnabled(enabled);
    }

    /**
     * Extracts a readable filename from a URI.
     */
    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int cut = path.lastIndexOf('/');
            if (cut != -1) return path.substring(cut + 1);
        }
        return uri.toString();
    }

    /**
     * Releases MediaPlayer resources.
     */
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            stopSeekBarUpdate();
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ────────────────────────────────────────────
    //  LIFECYCLE
    // ────────────────────────────────────────────

    @Override
    protected void onPause() {
        super.onPause();
        // Pause playback when app goes to background
        if (currentMode.equals("audio") && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopSeekBarUpdate();
        }
        if (currentMode.equals("video") && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        videoView.stopPlayback();
        stopSeekBarUpdate();
    }
}