package jp.openfab.heartshooter.mobile;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;

import android.os.Handler;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mlkcca.client.DataElement;
import com.mlkcca.client.DataElementValue;
import com.mlkcca.client.DataStore;
import com.mlkcca.client.Streaming;
import com.mlkcca.client.StreamingListener;
import com.mlkcca.client.MilkCocoa;
import com.mlkcca.client.DataStoreEventListener;


public class MainActivity extends Activity implements DataStoreEventListener {

    private EditText editText;
    private ArrayAdapter<String> adapter;
    private MilkCocoa milkcocoa;
    private Handler handler = new Handler();
    private DataStore messagesDataStore;

    Button startButton;
    Button stopButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        editText = (EditText)findViewById(R.id.editText1);

        connect();

        startButton = (Button)findViewById(R.id.start_button);
        stopButton  = (Button)findViewById(R.id.stop_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getBaseContext(),ListenerService.class));
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getBaseContext(), ListenerService.class));
            }
        });
    }

    private void connect() {
        this.milkcocoa = new MilkCocoa("xxxxxxxxxx");
        this.messagesDataStore = this.milkcocoa.dataStore("heartbeat");
        Streaming stream = this.messagesDataStore.streaming();
        stream.size(25);
        stream.sort("desc");
        stream.addStreamingListener(new StreamingListener() {

                @Override
                public void onData(ArrayList<DataElement> arg0) {
                    final ArrayList<DataElement> messages = arg0;

                    new Thread(new Runnable() {
                        public void run() {
                            handler.post(new Runnable() {
                                public void run() {
                                    for (int i = 0; i < messages.size(); i++) {
                                        adapter.insert(messages.get(i).getValue("heartbeat"), i);
                                    }
                                }
                            });
                        }
                    }).start();
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        stream.next();

        this.messagesDataStore.addDataStoreEventListener(this);
        this.messagesDataStore.on("push");
    }

    public void sendServer(String text) {
        DataElementValue params = new DataElementValue();

        Date date = new Date();
        params.put("date", date.toString());
        params.put("heartbeat", text);

        this.messagesDataStore.push(params);
    }

    public void sendEvent(View view){
        if (editText.getText().toString().length() == 0) {
            return;
        }

        String text = editText.getText().toString();
        sendServer(text);
        editText.setText("");
    }

    @Override
    public void onPushed(DataElement dataElement) {
        final DataElement pushed = dataElement;
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        String content = pushed.getValue("heartbeat");
                        adapter.insert(content, 0);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onSetted(DataElement dataElement) {

    }

    @Override
    public void onSended(DataElement dataElement) {

    }

    @Override
    public void onRemoved(DataElement dataElement) {

    }
}

