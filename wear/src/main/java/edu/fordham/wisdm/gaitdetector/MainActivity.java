package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    /**
     * String for tagging purposes
     */
    private final String TAG = "MainActivity";

    /**
     * String to identify the custom intent from the ListenerService
     */
    private static final String LISTENER_INTENT = "listener_intent";

    /**
     * String to initiate communication between phone and watch.
     */
    private static final String START_SAMPLING = "/start-sampling";

    /**
     * String to stop sampling on the wearable
     */
    private static final String STOP_SAMPLING = "/stop-sampling";

    /**
     * TextView to display messages indicating status
     */
    private TextView mTextView;

    /**
     * Broadcast receiver to update the UI when the ListenerService receives messages from the phone
     */
    private BroadcastReceiver mMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.message);
            }
        });

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");

                if (message.equals(START_SAMPLING)){
                    mTextView.setText("Sampling sensors...");
                } else if (message.equals(STOP_SAMPLING)){
                    mTextView.setText("Sampling completed");
                }
            }
        };

        // Register a LocalBroadcastManager to get messages from the ListenerService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter(LISTENER_INTENT));
    }


    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


}
