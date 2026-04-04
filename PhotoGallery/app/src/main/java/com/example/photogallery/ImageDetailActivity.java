package com.example.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ImageDetailActivity — Shows full details of a selected image.
 *
 * Displays:
 *   - Full image preview
 *   - File name
 *   - Full file path
 *   - File size (in KB / MB)
 *   - Date taken (last modified date)
 *
 * Also provides a Delete button with a confirmation AlertDialog.
 * After deletion, the user is sent back to the Gallery screen.
 */
public class ImageDetailActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private TextView tvName, tvPath, tvSize, tvDate;
    private Button btnDelete;

    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Image Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        ivPreview = findViewById(R.id.ivPreview);
        tvName    = findViewById(R.id.tvName);
        tvPath    = findViewById(R.id.tvPath);
        tvSize    = findViewById(R.id.tvSize);
        tvDate    = findViewById(R.id.tvDate);
        btnDelete = findViewById(R.id.btnDelete);

        // Get image path passed from GalleryActivity
        String imagePath = getIntent().getStringExtra("image_path");
        if (imagePath == null) {
            Toast.makeText(this, "Image path not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            Toast.makeText(this, "Image file does not exist", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display image and details
        displayImage();
        displayDetails();

        // Delete button — shows confirmation dialog first
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    /**
     * Loads and displays the full image in the ImageView.
     * Uses downsampling to avoid OutOfMemoryError on large photos.
     */
    private void displayImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        // Downsample to max 1080px wide
        options.inSampleSize    = calculateInSampleSize(options, 1080, 1080);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        if (bitmap != null) {
            ivPreview.setImageBitmap(bitmap);
        } else {
            ivPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    /**
     * Fills in the detail TextViews with file metadata.
     */
    private void displayDetails() {
        // File name
        tvName.setText("📄 Name:  " + imageFile.getName());

        // Full path
        tvPath.setText("📁 Path:  " + imageFile.getAbsolutePath());

        // File size — show in KB or MB
        long sizeBytes = imageFile.length();
        tvSize.setText("💾 Size:  " + formatFileSize(sizeBytes));

        // Date taken — use last modified timestamp
        long lastModified = imageFile.lastModified();
        String dateStr = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(new Date(lastModified));
        tvDate.setText("📅 Date:  " + dateStr);
    }

    /**
     * Shows a confirmation AlertDialog before deleting the image.
     */
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete\n\"" + imageFile.getName() + "\"?\n\nThis action cannot be undone.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", (dialog, which) -> deleteImage())
                .setNegativeButton("Cancel", null) // dismiss dialog, do nothing
                .show();
    }

    /**
     * Deletes the image file and navigates back to the Gallery.
     */
    private void deleteImage() {
        if (imageFile.delete()) {
            Toast.makeText(this, "✅ Image deleted successfully", Toast.LENGTH_SHORT).show();
            // Go back to Gallery (which will reload and no longer show this image)
            finish();
        } else {
            Toast.makeText(this, "❌ Failed to delete image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Formats file size from bytes to a human-readable string.
     */
    private String formatFileSize(long bytes) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return df.format(bytes / 1024.0) + " KB";
        } else {
            return df.format(bytes / (1024.0 * 1024.0)) + " MB";
        }
    }

    /**
     * Calculates downsampling factor for BitmapFactory.
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width  = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth  = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
