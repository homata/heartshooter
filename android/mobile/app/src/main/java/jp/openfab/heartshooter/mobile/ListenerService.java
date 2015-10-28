package jp.openfab.heartshooter.mobile;

import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import android.content.Intent;
import android.util.Log;

public class ListenerService extends WearableListenerService {

    final static String TAG = "ListenerService";

    /*
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    */

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived");
        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        Log.d(TAG, "showToast : " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        sendBroadCast(message);
    }

    protected void sendBroadCast(String message) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("message", message);
        broadcastIntent.setAction("UPDATE_ACTION");
        getBaseContext().sendBroadcast(broadcastIntent);

    }

}
