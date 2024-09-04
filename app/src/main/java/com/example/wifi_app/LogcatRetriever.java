package com.example.wifi_app;

// LogcatRetriever.java

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LogcatRetriever {

    public String getLogs() {
        StringBuilder log = new StringBuilder();

        try {
            String command = "logcat -d " + "WiFiScan" + ":V *:S";  // ":V" stands for Verbose, "*:S" suppresses other tags
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line).append("\n");
            }

        } catch (Exception e) {
            log.append("Failed to retrieve logcat logs: ").append(e.getMessage());
        }

        return log.toString();
    }
}
