package com.example.photogallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * GalleryActivity — Shows all images in a selected folder as a grid.
 *
 * - Default folder: Pictures/PhotoGalleryApp (where camera saves)
 * - User can also pick any folder via the "Change Folder" button
 * - Clicking an image opens ImageDetailActivity
 */
public class GalleryActivity extends AppCompatActivity {

    private GridView gridView;
    private TextView tvFolderPath, tvEmptyMessage;
    private ImageGridAdapter adapter;

    // List of image file paths to display
    private List<String> imagePaths = new ArrayList<>();

    // Currently displayed folder
    private File currentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🖼️ Image Gallery");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        gridView       = findViewById(R.id.gridView);
        tvFolderPath   = findViewById(R.id.tvFolderPath);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        // "Change Folder" button — lets user pick a different folder
        findViewById(R.id.btnChangeFolder).setOnClickListener(v -> pickFolder());

        // Setup grid adapter
        adapter = new ImageGridAdapter(this, imagePaths);
        gridView.setAdapter(adapter);

        // On image click → open detail screen
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String imagePath = imagePaths.get(position);
            Intent intent = new Intent(GalleryActivity.this, ImageDetailActivity.class);
            intent.putExtra("image_path", imagePath);
            startActivity(intent);
        });

        // Load default folder (PhotoGalleryApp)
        currentFolder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PhotoGalleryApp"
        );

        loadImagesFromFolder(currentFolder);
    }

    /**
     * Opens a simple folder picker dialog with common folder options.
     */
    private void pickFolder() {
        // Common folders to choose from
        final String[] folderNames = {
                "PhotoGalleryApp (Default)",
                "DCIM/Camera",
                "Pictures",
                "Downloads"
        };

        final File[] folders = {
                new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "PhotoGalleryApp"),
                new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), "Camera"),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choose a Folder")
                .setItems(folderNames, (dialog, which) -> {
                    currentFolder = folders[which];
                    loadImagesFromFolder(currentFolder);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Scans the given folder for image files (.jpg, .jpeg, .png, .webp, .gif)
     * and updates the grid view.
     */
    public void loadImagesFromFolder(File folder) {
        imagePaths.clear();

        if (folder == null || !folder.exists()) {
            tvFolderPath.setText("Folder: " + (folder != null ? folder.getAbsolutePath() : "N/A"));
            tvEmptyMessage.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("📁 Folder does not exist yet.\nTake a photo first!");
            adapter.notifyDataSetChanged();
            return;
        }

        tvFolderPath.setText("📂 " + folder.getAbsolutePath());

        // Get all image files in folder
        File[] files = folder.listFiles(file ->
                file.isFile() && isImageFile(file.getName())
        );

        if (files != null && files.length > 0) {
            for (File file : files) {
                imagePaths.add(file.getAbsolutePath());
            }
            tvEmptyMessage.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("🖼️ No images found in this folder.");
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Checks if a filename has an image extension.
     */
    private boolean isImageFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".webp")
                || lower.endsWith(".gif");
    }

    /**
     * Reload images when returning from ImageDetailActivity
     * (in case an image was deleted).
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (currentFolder != null) {
            loadImagesFromFolder(currentFolder);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
