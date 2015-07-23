package edu.fordham.wisdm.gaitdetector;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * The ListenerService class listens for connections from the phone.
 *
 * Once a connection is established, the ListenerService initiates the ...
 * @author Luigi Patruno
 */
public class ListenerService extends WearableListenerService {

    /**
     * String for tagging purposes
     */
    private static final String TAG = "ListenerService";

    /**
     * String to initiate communication between phone and watch.
     */
    private static final String START_SAMPLING = "/start-sampling";

    /**
     * String to stop sampling on the wearable
     */
    private static final String STOP_SAMPLING = "/stop-sampling";

    /**
     * String to identify the custom intent from the ListenerService
     */
    private static final String LISTENER_INTENT = "listener_intent";


    /**
     * Callback method when wearable receives a message from the phone.
     *
     * If START_SAMPLING, start sampling the wearable sensors
     * IF STOP_SAMPLING, stop sampling the wearable sensors and send the data to the phone
     *
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if (messageEvent.getPath().equals(START_SAMPLING)) {

            // Send a message to the UI
            sendUiMessage(START_SAMPLING);

        } else if (messageEvent.getPath().equals(STOP_SAMPLING)) {
            // Send a message to the UI
            sendUiMessage(STOP_SAMPLING);
        }
    }

    /**
     * Send a message to the Main Activity through a LocalBroadcastManager
     * @param message
     */
    private void sendUiMessage(String message){
        Intent intent = new Intent(LISTENER_INTENT);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}
