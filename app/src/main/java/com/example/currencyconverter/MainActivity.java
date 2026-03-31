package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText amount;
    Spinner fromCurrency, toCurrency;
    TextView result;
    Button convertBtn, settingsBtn;

    String[] currencies = {"INR", "USD", "EUR", "JPY"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amount = findViewById(R.id.amount);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        result = findViewById(R.id.result);
        convertBtn = findViewById(R.id.convertBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, currencies);

        fromCurrency.setAdapter(adapter);
        toCurrency.setAdapter(adapter);

        convertBtn.setOnClickListener(v -> {

            // Animation
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1).scaleY(1).setDuration(100));

            String value = amount.getText().toString();

            if (value.isEmpty()) {
                result.setText("Enter amount");
                return;
            }

            double input = Double.parseDouble(value);

            String from = fromCurrency.getSelectedItem().toString();
            String to = toCurrency.getSelectedItem().toString();

            double output = convertCurrency(input, from, to);

            result.setText(from + " → " + to + " : " + String.format("%.2f", output));
        });

        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private double convertCurrency(double amount, String from, String to) {

        double inINR = 0;

        switch (from) {
            case "INR": inINR = amount; break;
            case "USD": inINR = amount * 83; break;
            case "EUR": inINR = amount * 90; break;
            case "JPY": inINR = amount * 0.55; break;
        }

        switch (to) {
            case "INR": return inINR;
            case "USD": return inINR / 83;
            case "EUR": return inINR / 90;
            case "JPY": return inINR / 0.55;
        }

        return 0;
    }
}