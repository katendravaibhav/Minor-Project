package com.example.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start the background SocketService
        startService(new Intent(this, SocketService.class));
        // Close the activity (no UI needed)
        finish();
    }
}
