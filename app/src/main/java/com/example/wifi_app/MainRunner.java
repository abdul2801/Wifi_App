package com.example.wifi_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Handler;
import android.os.Looper;

public class MainRunner extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private LogcatRetriever logcatRetriever;
    private TextView logcatTextView;
    private ScrollView scrollView;
    private Handler handler;

    private EditText username;
    private EditText password;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Check and request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                        != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)

        {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        } else {
            startWifiMonitoringService();

//            logcatRetriever = new LogcatRetriever();
//            logcatTextView = findViewById(R.id.logcatTextView);
//            scrollView = findViewById(R.id.scrollView);
//            handler = new Handler();
//            startLogUpdates();

            username = findViewById(R.id.username);
            password = findViewById(R.id.password);
            login = findViewById(R.id.login);

//            set the username and password from the shared preferences
            SimpleSecureStorage simpleSecureStorage = new SimpleSecureStorage(MainRunner.this);
            username.setText(simpleSecureStorage.getUsername());
            password.setText(simpleSecureStorage.getPassword());

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user = username.getText().toString();
                    String pass = password.getText().toString();

                    simpleSecureStorage.storeCredentials(user, pass);




                }
            });

        }
    }

    private void startLogUpdates() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String logs = logcatRetriever.getLogs();
                logcatTextView.setText(logs);

                // Scroll to the bottom after updating the TextView
//                scrollView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                    }
//                });

                // Schedule the next update after 1 second
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop updating logs when the activity is destroyed
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted, starting Wi-Fi monitoring", Toast.LENGTH_SHORT).show();
                startWifiMonitoringService();
            } else {
                Toast.makeText(this, "Permissions denied. The app requires these permissions to function.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startWifiMonitoringService() {
        Toast.makeText(this, "Starting Wi-Fi monitoring service", Toast.LENGTH_SHORT).show();
        // Wait for Looper to be ready

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Intent serviceIntent = new Intent(MainRunner.this, WiFiMonitorService.class);
                startService(serviceIntent);

            }

        });
    }
}
