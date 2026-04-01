package com.example.currencyconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * SettingsActivity - Lets the user toggle between Light and Dark themes.
 * The choice is saved to SharedPreferences and applied immediately via
 * AppCompatDelegate, which recreates all activities automatically.
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private TextView tvThemeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Show the back arrow in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        switchDarkMode = findViewById(R.id.switchDarkMode);
        tvThemeLabel   = findViewById(R.id.tvThemeLabel);

        // Read saved preference
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);

        // Reflect current state (suppress listener during init)
        switchDarkMode.setChecked(isDark);
        updateLabel(isDark);

        // Listen for user toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Persist the choice
            prefs.edit().putBoolean("dark_mode", isChecked).apply();

            // Apply theme — this triggers recreation of all activities
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );

            updateLabel(isChecked);

            // Recreate this activity so the new theme renders here too
            recreate();
        });
    }

    /**
     * Updates the subtitle label under the switch to reflect current state.
     */
    private void updateLabel(boolean isDark) {
        tvThemeLabel.setText(isDark ? "Dark Mode (ON)" : "Light Mode (ON)");
    }

    // Handle the toolbar back arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}