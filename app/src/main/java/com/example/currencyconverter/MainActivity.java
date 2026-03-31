package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

        // 🔥 FIXED SPINNER ADAPTER (WHITE BG + BLACK TEXT)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(getResources().getColor(android.R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);

                // 🔥 MAIN FIX
                view.setTextColor(getResources().getColor(android.R.color.black));
                view.setBackgroundColor(getResources().getColor(android.R.color.white));

                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromCurrency.setAdapter(adapter);
        toCurrency.setAdapter(adapter);

        // Convert Logic
        convertBtn.setOnClickListener(v -> {

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

        // Settings Button
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