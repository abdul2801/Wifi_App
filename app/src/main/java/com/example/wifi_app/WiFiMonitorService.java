package com.example.wifi_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WiFiMonitorService extends Service {
    private static final String CHANNEL_ID = "WiFiMonitorServiceChannel";
    private OkHttpClient client = new OkHttpClient()
            .newBuilder()
//            .retryOnConnectionFailure(true)

            .build();


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        // Initialize Wi-Fi monitoring logic here
        startForegroundService();
        initWifiManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start monitoring Wi-Fi
        Log.d("WiFiMonitorService", "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources
        Log.d("WiFiMonitorService", "Service stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initWifiManager() {
        Log.d("WiFiMonitorService", "Initializing Wi-Fi manager");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Handle Wi-Fi scan results or state changes
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Log.d("WiFiScan", "Location permission not granted");
                        return;


                    }
                    List<ScanResult> results = wifiManager.getScanResults();


                    if (results.isEmpty()) {
                        Log.d("WiFiScan", "No Wi-Fi networks found");
                    }

//                    gget the connected wifi
                    Log.d("WiFiScan", "Connected Wi-Fi: " + wifiManager.getConnectionInfo().toString());
                    final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    for (Network net : cm.getAllNetworks()) {
                        if (Objects.requireNonNull(cm.getNetworkInfo(net)).getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.d("WiFiScan", "Connected Wi-Fi network: " + cm.getNetworkInfo(net));
                            cm.bindProcessToNetwork(net);
                        }
                    }

//                    if (cm.getActiveNetwork() != null) {
                        if (wifiManager.getConnectionInfo().getSSID().equals("\"" + "IIITKottayam" + "\"")) {
                            Log.d("WiFiScan", "Connected to IIITKottayam");
                            checkCaptivePortal();
                        } else {
                            Log.d("WiFiScan", "Not connected to IIITKottayam");


                    }



                }
            }
        };
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan(); // Start scanning for Wi-Fi networks
    }
    private void checkCaptivePortal() {
        Log.d("WiFiScan", "Checking captive portal");
//                sleep for few seconds



        Request request = new Request.Builder()
                .url("http://clients3.google.com/generate_204")
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Accept-Encoding",  "deflate")
                .header("Connection", "close")


                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CaptivePortal", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("CaptivePortal", "Unexpected response: " + response);
                    return;
                }
                assert response.body() != null;
                String body = response.body().string();
                Log.d("CaptivePortal", "Response: " + body);
                String postUrl = extractPostUrlFromResponse(body);
                if (postUrl.isEmpty()) {
                    SimpleSecureStorage simpleSecureStorage = new SimpleSecureStorage(WiFiMonitorService.this);
                    Log.d("CaptivePortal", simpleSecureStorage.getUsername() + " " + simpleSecureStorage.getPassword());
                    Log.d("CaptivePortal", "No POST URL found");
                    return;
                }
                Log.d("CaptivePortal", "POST URL: " + postUrl);
                Uri uri = Uri.parse(postUrl);
                String magic = uri.getQuery();
//                without uri url


            getRequest(postUrl);

            SimpleSecureStorage simpleSecureStorage = new SimpleSecureStorage(WiFiMonitorService.this);
            Log.d("CaptivePortal", simpleSecureStorage.getUsername() + " " + simpleSecureStorage.getPassword());

                if(simpleSecureStorage.getUsername() == null || simpleSecureStorage.getPassword() == null) {
                    return;
                }




                sendPostRequest(uri.buildUpon()
                        .clearQuery()
                        .path(Objects.requireNonNull(uri.getPath()).substring(0, uri.getPath().lastIndexOf('/')))
                        .build()
                        .toString(), magic, simpleSecureStorage.getUsername(), simpleSecureStorage.getPassword());

            }
        });
    }
    private void getRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Accept-Encoding",  "deflate")
                .header("Connection", "keep-alive")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("GetRequest", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("GetRequest", "Unexpected response: " + response);
                    return;
                }
                Log.d("GetRequest" , response.body().string());






            }
        });

    }

    private String extractPostUrlFromResponse(String response) {
        String regex = "window\\.location=\"(http[^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {

            return matcher.group(1);
        }
        return "";

    }

    public void sendPostRequest(String urlString, String magic, String username, String password) {
        // Create the POST data
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String postData = String.format("4Tredir=%s&magic=%s&username=%s&password=%s",
                urlEncode("http://clients3.google.com/generate_204"),
                urlEncode(magic),
                urlEncode(username),
                urlEncode(password)
        );
        Log.d("POSTRequest", "Sending POST request to " + urlString + " with data: " + postData);

        RequestBody body = RequestBody.create(postData, MediaType.parse("application/x-www-form-urlencoded"));

        Request request = new Request.Builder()
                .url(urlString)
                .post(body)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .addHeader("Accept-Language", "en-US,en;q=0.9")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Origin", "http://172.16.222.1:1000")
                .addHeader("Referer", "http://172.16.222.1:1000/")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("POSTRequest", "Error sending POST request", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("POSTRequest", "Unexpected code " + response);
                } else {
                    // Handle the response
                    String responseData = response.body().string();
                    Log.d("POSTRequest", "Response: " + responseData);
                }
            }
        });
    }

    private String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }





    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WiFi Monitor Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startForegroundService() {
        Notification notification = null;
        Log.d("WiFiMonitorService", "Starting foreground service");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Wi-Fi Monitor Service")
                    .setContentText("Monitoring Wi-Fi connectivity")
                    .setSmallIcon(R.drawable.wifi)
                    .build();

        }

        startForeground(1, notification);
    }
}
