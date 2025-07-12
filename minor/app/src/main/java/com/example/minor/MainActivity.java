package com.example.minor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private boolean isRecording = false;
    private final int RECORD_DURATION = 10; // Change duration if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    try {
                        MicManager.startRecording(RECORD_DURATION);
                        recordButton.setText("Stop Recording");
                        isRecording = true;
                    } catch (Exception e) {
                        Log.e("MainActivity", "Error starting recording: " + e.getMessage());
                    }
                } else {
                    MicManager.stopRecording();
                    recordButton.setText("Start Recording");
                    isRecording = false;
                }
            }
        });
    }
}
