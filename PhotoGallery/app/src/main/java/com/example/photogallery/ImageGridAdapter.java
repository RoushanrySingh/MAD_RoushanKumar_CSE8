package com.example.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * ImageGridAdapter — Custom BaseAdapter for the GridView in GalleryActivity.
 *
 * Loads each image as a downsampled thumbnail to avoid OutOfMemoryError.
 * Each grid cell shows a square image thumbnail.
 */
public class ImageGridAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> imagePaths; // list of absolute file paths

    // Thumbnail dimensions (square cells)
    private static final int THUMB_SIZE = 300; // px

    public ImageGridAdapter(Context context, List<String> imagePaths) {
        this.context    = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // Create a new square ImageView for each grid cell
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(THUMB_SIZE, THUMB_SIZE));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load a downsampled bitmap to avoid memory issues
        String path = imagePaths.get(position);
        Bitmap thumbnail = decodeSampledBitmap(path, THUMB_SIZE, THUMB_SIZE);

        if (thumbnail != null) {
            imageView.setImageBitmap(thumbnail);
        } else {
            // Fallback placeholder if image cannot be decoded
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        return imageView;
    }

    /**
     * Decodes a bitmap from file path with downsampling to avoid OutOfMemoryError.
     *
     * @param filePath  absolute path to the image file
     * @param reqWidth  required width in pixels
     * @param reqHeight required height in pixels
     * @return downsampled Bitmap or null on error
     */
    private Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        try {
            // First pass: decode only bounds (no pixels loaded)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate the sample size
            options.inSampleSize    = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            // Second pass: decode with sample size applied
            return BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Calculates the largest inSampleSize value that keeps the image
     * larger than the required dimensions.
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height     = options.outHeight;
        int width      = options.outWidth;
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
}
