package com.example.photogallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity — Home screen of the Photo Gallery App.
 *
 * Features:
 *   - Take a photo using the device camera (saves to a chosen folder)
 *   - Open the Gallery screen to browse images in a folder
 */
public class MainActivity extends AppCompatActivity {

    // Request codes
    private static final int REQUEST_CAMERA         = 100;
    private static final int REQUEST_PERMISSIONS    = 200;
    private static final int REQUEST_PICK_FOLDER    = 300;

    // URI of the photo being taken (used to pass to camera intent)
    private Uri photoUri;
    // File reference for the captured photo
    private File photoFile;

    // UI
    private Button btnTakePhoto, btnOpenGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("📷 Photo Gallery");
        }

        btnTakePhoto   = findViewById(R.id.btnTakePhoto);
        btnOpenGallery = findViewById(R.id.btnOpenGallery);

        // Take Photo button
        btnTakePhoto.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                openCamera();
            }
        });

        // Open Gallery button
        btnOpenGallery.setOnClickListener(v -> {
            // Open folder picker so user can choose a folder
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Checks required permissions and requests them if not granted.
     * Returns true if all permissions are already granted.
     */
    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        // Camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        // Storage permissions — Android 13+ uses READ_MEDIA_IMAGES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toArray(new String[0]),
                    REQUEST_PERMISSIONS
            );
            return false;
        }

        return true; // all permissions granted
    }

    /**
     * Opens the device camera. Creates a file to store the photo first,
     * then launches the camera intent with the file URI.
     */
    private void openCamera() {
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Cannot create image file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Get URI via FileProvider (required for Android 7+)
        photoUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                photoFile
        );

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "No camera app found on this device", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a uniquely named image file in the app's Pictures directory.
     * File name format: IMG_20240101_120000.jpg
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String fileName  = "IMG_" + timeStamp;

        // Save to Pictures/PhotoGalleryApp folder
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PhotoGalleryApp"
        );

        // Create the folder if it doesn't exist
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    /**
     * Called when camera returns after taking a photo.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (photoFile != null && photoFile.exists()) {
                Toast.makeText(this,
                        "✅ Photo saved!\n" + photoFile.getAbsolutePath(),
                        Toast.LENGTH_LONG).show();

                // Notify media scanner so photo shows in Gallery app
                Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScan.setData(Uri.fromFile(photoFile));
                sendBroadcast(mediaScan);
            }
        }
    }

    /**
     * Handles permission request results.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                openCamera();
            } else {
                Toast.makeText(this,
                        "Permissions are required to use the camera and access storage.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}