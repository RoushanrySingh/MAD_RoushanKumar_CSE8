package com.example.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * MainActivity - Sensor Data Reader
 *
 * Reads and displays real-time data from:
 *   1. Accelerometer  — X, Y, Z axis values (m/s²)
 *   2. Light Sensor   — Ambient light level (lux)
 *   3. Proximity      — Distance from object (cm)
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Sensor Manager — gateway to all device sensors
    private SensorManager sensorManager;

    // Sensor objects
    private Sensor accelerometer;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    // ── Accelerometer UI ──
    private TextView tvAccelX, tvAccelY, tvAccelZ, tvAccelStatus;

    // ── Light Sensor UI ──
    private TextView tvLight, tvLightStatus, tvLightDesc;

    // ── Proximity Sensor UI ──
    private TextView tvProximity, tvProximityStatus, tvProximityDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sensor Data");
        }

        // ── Bind Accelerometer views ──
        tvAccelX      = findViewById(R.id.tvAccelX);
        tvAccelY      = findViewById(R.id.tvAccelY);
        tvAccelZ      = findViewById(R.id.tvAccelZ);
        tvAccelStatus = findViewById(R.id.tvAccelStatus);

        // ── Bind Light views ──
        tvLight       = findViewById(R.id.tvLight);
        tvLightStatus = findViewById(R.id.tvLightStatus);
        tvLightDesc   = findViewById(R.id.tvLightDesc);

        // ── Bind Proximity views ──
        tvProximity       = findViewById(R.id.tvProximity);
        tvProximityStatus = findViewById(R.id.tvProximityStatus);
        tvProximityDesc   = findViewById(R.id.tvProximityDesc);

        // ── Get SensorManager ──
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // ── Get each sensor (null if device doesn't have it) ──
        accelerometer  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor    = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // ── Check availability and update status labels ──
        checkSensorAvailability();
    }

    /**
     * Checks if each sensor is available on this device and updates status labels.
     */
    private void checkSensorAvailability() {
        if (accelerometer == null) {
            tvAccelStatus.setText("❌ Not available on this device");
        } else {
            tvAccelStatus.setText("✅ " + accelerometer.getName());
        }

        if (lightSensor == null) {
            tvLightStatus.setText("❌ Not available on this device");
        } else {
            tvLightStatus.setText("✅ " + lightSensor.getName());
        }

        if (proximitySensor == null) {
            tvProximityStatus.setText("❌ Not available on this device");
        } else {
            tvProximityStatus.setText("✅ " + proximitySensor.getName());
        }
    }

    /**
     * Register all sensor listeners when activity is in foreground.
     * SENSOR_DELAY_UI = ~60ms update rate, good for display purposes.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Unregister listeners when activity goes to background — saves battery.
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Called every time a sensor value changes.
     * This is the core callback — update UI here.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {

            // ── Accelerometer ──
            // event.values[0] = X axis (m/s²)
            // event.values[1] = Y axis (m/s²)
            // event.values[2] = Z axis (m/s²)
            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                tvAccelX.setText(String.format("X:  %.4f m/s²", x));
                tvAccelY.setText(String.format("Y:  %.4f m/s²", y));
                tvAccelZ.setText(String.format("Z:  %.4f m/s²", z));
                break;

            // ── Light Sensor ──
            // event.values[0] = ambient light in lux
            case Sensor.TYPE_LIGHT:
                float lux = event.values[0];
                tvLight.setText(String.format("%.1f lux", lux));
                tvLightDesc.setText(getLightDescription(lux));
                break;

            // ── Proximity Sensor ──
            // event.values[0] = distance in cm
            // Many devices only return 0 (near) or max range (far)
            case Sensor.TYPE_PROXIMITY:
                float distance = event.values[0];
                tvProximity.setText(String.format("%.1f cm", distance));

                // Most phones: 0 = object near, >0 = far
                if (distance == 0) {
                    tvProximityDesc.setText("📱 Object NEAR (e.g. held to ear)");
                } else {
                    tvProximityDesc.setText("🌌 Object FAR / No object detected");
                }
                break;
        }
    }

    /**
     * Returns a human-readable description of the light level in lux.
     */
    private String getLightDescription(float lux) {
        if (lux < 1)    return "🌑 Very Dark (pitch black)";
        if (lux < 50)   return "🕯️ Dim (candle light)";
        if (lux < 200)  return "🏠 Indoor lighting";
        if (lux < 1000) return "💡 Bright room / office";
        if (lux < 5000) return "☁️ Overcast daylight";
        return                  "☀️ Direct sunlight";
    }

    /**
     * Called when sensor accuracy changes — required by SensorEventListener interface.
     * Not needed for this app but must be implemented.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}