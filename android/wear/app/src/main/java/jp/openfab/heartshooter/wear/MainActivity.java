package jp.openfab.heartshooter.wear;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private TextView mTextView;

    private final String TAG = "HRM";

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final String MESSAGE = "Hello Wear!";

    private GoogleApiClient client;
    private String nodeId;
    private String sendHrm;

    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sensor and sensor manager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            Log.d(TAG, sensor.getName() + "/" + sensor.getType() + "/" + sensor.getVendor() + "/" + sensor.getMaximumRange() + "/" + sensor.getMinDelay() + "/" + sensor.getPower() + "/" + sensor.getResolution());
        }

        initApi();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    final SensorEventListener mHeartListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int ii = (int)event.values[0];
            String text = String.valueOf(ii);

            Log.d(TAG, "Heart Rate Changed : " + event.values[0] + " : int => " + text);
            if (mTextView != null) {
                mTextView.setText(text);
                mTextView.setBackgroundColor(0xFF000000);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged : " + accuracy);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Sensor heart = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(mHeartListener, heart, 3);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mHeartListener);
    }

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }
    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
    private void sendToast(String msg) {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE, null);
                    client.disconnect();
                }
            }).start();

        }
    }

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
    private void sendText(String heartbeat) {
        sendHrm = heartbeat;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "sendText \"" + sendHrm + "\" to handheld");
                for (Node node : Wearable.NodeApi.getConnectedNodes(client).await().getNodes()){
                    Log.v(TAG, "sending to node:" + node.getId());

                    Wearable.MessageApi.sendMessage(client, node.getId(), sendHrm, null)
                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    Log.v(TAG, sendMessageResult.toString());
                                }
                        });
                }
            }
        }).start();
    }
}
