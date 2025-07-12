package com.example.minor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class RecorderService extends Service {
    private MediaRecorder mediaRecorder;
    private String outputFilePath;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    private void startRecording() {
        try {
            outputFilePath = getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/recording_" + System.currentTimeMillis() + ".aac";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(outputFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.d("RecorderService", "🎙️ Recording Started: " + outputFilePath);
        } catch (IOException e) {
            Log.e("RecorderService", "❌ Error starting recording: " + e.getMessage());
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Log.d("RecorderService", "⏹️ Recording Stopped. File saved: " + outputFilePath);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "RecordingChannel",
                    "Background Audio Recording",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification getNotification() {
        return new Notification.Builder(this, "RecordingChannel")
                .setContentTitle("Recording Audio")
                .setContentText("Background recording in progress...")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
