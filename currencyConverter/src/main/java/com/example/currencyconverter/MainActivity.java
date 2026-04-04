package com.example.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * MainActivity - Currency Converter main screen.
 * Supports INR, USD, JPY, EUR conversion with swap functionality.
 */
public class MainActivity extends AppCompatActivity {

    // UI components
    private EditText etAmount;
    private Spinner spinnerFrom, spinnerTo;
    private Button btnConvert, btnSwap;
    private TextView tvResult, tvRate;

    // Currencies supported
    private final String[] currencies = {"INR", "USD", "JPY", "EUR"};

    // Exchange rates relative to 1 INR (base currency)
    // 1 INR = x <currency>
    private final double[] ratesFromINR = {
            1.0,      // INR
            0.012,    // USD  (1 INR = 0.012 USD)
            1.81,     // JPY  (1 INR = 1.81 JPY)
            0.011     // EUR  (1 INR = 0.011 EUR)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme BEFORE setContentView
        applyThemeFromPrefs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Currency Converter");
        }

        // Bind UI components
        etAmount    = findViewById(R.id.etAmount);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo   = findViewById(R.id.spinnerTo);
        btnConvert  = findViewById(R.id.btnConvert);
        btnSwap     = findViewById(R.id.btnSwap);
        tvResult    = findViewById(R.id.tvResult);
        tvRate      = findViewById(R.id.tvRate);

        // Setup adapters for spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Default: From INR (index 0), To USD (index 1)
        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(1);

        // Update rate label whenever spinner changes
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRateDisplay();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerFrom.setOnItemSelectedListener(spinnerListener);
        spinnerTo.setOnItemSelectedListener(spinnerListener);

        // Convert button
        btnConvert.setOnClickListener(v -> convertCurrency());

        // Swap button — swaps From and To currencies
        btnSwap.setOnClickListener(v -> {
            int fromPos = spinnerFrom.getSelectedItemPosition();
            int toPos   = spinnerTo.getSelectedItemPosition();
            spinnerFrom.setSelection(toPos);
            spinnerTo.setSelection(fromPos);
            // Re-convert if amount is already entered
            if (etAmount.getText().length() > 0) {
                convertCurrency();
            }
        });

        // Show initial rate
        updateRateDisplay();
    }

    /**
     * Reads amount input, calculates conversion via INR base rate, displays result.
     */
    private void convertCurrency() {
        String amountStr = etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 0) {
            Toast.makeText(this, "Amount cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        int fromIndex = spinnerFrom.getSelectedItemPosition();
        int toIndex   = spinnerTo.getSelectedItemPosition();

        // Convert: amount -> INR -> target currency
        double inrValue = amount / ratesFromINR[fromIndex];
        double result   = inrValue * ratesFromINR[toIndex];

        String fromCurrency = currencies[fromIndex];
        String toCurrency   = currencies[toIndex];

        // JPY has no decimal places; others show 4
        String formattedResult = (toCurrency.equals("JPY"))
                ? String.format("%.0f", result)
                : String.format("%.4f", result);

        String formattedAmount = String.format("%.2f", amount);
        tvResult.setText(formattedAmount + " " + fromCurrency + " = " + formattedResult + " " + toCurrency);
    }

    /**
     * Displays the current exchange rate between selected currencies.
     */
    private void updateRateDisplay() {
        int fromIndex = spinnerFrom.getSelectedItemPosition();
        int toIndex   = spinnerTo.getSelectedItemPosition();

        double rate = ratesFromINR[toIndex] / ratesFromINR[fromIndex];

        String fromCurrency = currencies[fromIndex];
        String toCurrency   = currencies[toIndex];

        String formattedRate = (toCurrency.equals("JPY"))
                ? String.format("%.4f", rate)
                : String.format("%.6f", rate);

        tvRate.setText("1 " + fromCurrency + " = " + formattedRate + " " + toCurrency);
    }

    /**
     * Reads dark_mode flag from SharedPreferences and applies AppCompatDelegate mode.
     * Call this before super.onCreate() so the theme is set before the window is created.
     */
    private void applyThemeFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    // Inflate the options menu (toolbar gear icon → Settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle toolbar menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}