package com.example.se2_ez_mitterer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lambda expressions not supported by language level 7, using anonymous class instead :/
        findViewById(R.id.btnAbschicken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchServerResponse();
            }
        });

        findViewById(R.id.btnBerechne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    // Aufgabe 3: Ziffern der Größe nach sortieren, Primzahlen werden gestrichen
    private void calculate() {
        EditText txtMatNo = findViewById(R.id.txtMatrikelnummer);
        String inputMatNo = txtMatNo.getText().toString();

        if (inputMatNo == null || inputMatNo.isEmpty()) {
            // set the label to error message
            TextView lblResult = findViewById(R.id.lblAntwortResult);
            lblResult.setText("Es wurde keine Matrikelnummer eingegeben.");
            return;
        }

        // split each digit
        String[] matDigits = inputMatNo.split("");

        // use standard framework to sort the digits (hopefully thats okay and we are not meant to implement it ourself)
        Arrays.sort(matDigits);

        String[] primes = { "1", "3", "5", "7" };
        StringBuilder sb = new StringBuilder();

        // only append non primes to stringbuilder
        for (String digit : matDigits) {
            boolean isPrime = false;

            for (String prime : primes) {
                if (digit.equals(prime)) {
                    isPrime = true;
                    break;
                }
            }

            if (!isPrime) {
                sb.append(digit);
            }
        }
        // set the label to new text
        TextView lblResult = findViewById(R.id.lblAntwortResult);
        lblResult.setText("Das Ergebnis der Berechnung ist \"" + sb.toString() + "\".");
    }

    private void fetchServerResponse() {
        EditText txtMatNo = findViewById(R.id.txtMatrikelnummer);
        final String inputMatNo = txtMatNo.getText().toString();

        // deactivate button while fetching result
        Button button = findViewById(R.id.btnAbschicken);
        button.setEnabled(false);

        // android requires a handler, if you want to edit UI from another thread
        final Handler handler = new Handler();
        Runnable serverRunnable = new Runnable() {
            public void run() {
                // instance that handles the socket, connection and streams
                IsysTCPConnection connection = new IsysTCPConnection();

                // try catch in case there is a connection/IO exception
                try {
                    connection.connect();

                    String response = connection.getResponseFromMatrikelnummer(inputMatNo);

                    // let the main thread change the textfield (UI changes are not permitted on other threads than main thred)
                    handler.post(new UpdateResultViewRunnable(response));

                    connection.close();
                } catch (IOException ioEx) {
                    String errorMessage = ioEx.getMessage();

                    Log.e("myapplol", errorMessage);

                    // let the main thread change the textfield, so the user knows an error occured
                    handler.post(new UpdateResultViewRunnable("Error: " + errorMessage));
                }
            }
        };

        new Thread(serverRunnable).start();
    }

    private class UpdateResultViewRunnable implements Runnable {
        private String text;

        public UpdateResultViewRunnable(String newText) {
            this.text = newText;
        }

        @Override
        public void run() {
            // set the label to new text
            TextView lblResult = findViewById(R.id.lblAntwortResult);
            lblResult.setText(this.text);

            // activate the button again
            Button button = findViewById(R.id.btnAbschicken);
            button.setEnabled(true);
        }
    }
}
