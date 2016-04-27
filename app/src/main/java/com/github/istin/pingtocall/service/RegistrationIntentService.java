package com.github.istin.pingtocall.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.net.URLEncoder;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static final String SENT_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER";
    public static final String REGISTRATION_COMPLETE_ACTION = "REGISTRATION_COMPLETE_ACTION";
    public static final String REGISTRATION_ERROR_ACTION = "REGISTRATION_ERROR_ACTION";
    public static final String SENDER_ID = "1067813267795";
    public static final String BASE_URL = "http://ping-to-call.appspot.com/";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String message = null;
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            final String email = sharedPreferences.getString("email", null);
            final String pin = sharedPreferences.getString("pin", null);
            message = sendRegistrationToServer(token, email, pin);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
            Intent registrationError = new Intent(REGISTRATION_ERROR_ACTION);
            registrationError.putExtra("message", message + e.toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationError);
            return;
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE_ACTION);
        registrationComplete.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param pToken
     * @param pEmail
     */
    private String sendRegistrationToServer(String pToken, String pEmail, String pPin) throws IOException {
        final String url = BASE_URL + "reg?regId=" + URLEncoder.encode(pToken, "utf-8") + "&email=" + URLEncoder.encode(pEmail, "utf-8") + "&pin=" + URLEncoder.encode(pPin, "utf-8");
        Log.d(TAG, url);
        return HttpRequest.get(url).body();
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }


    public static void ping(final Activity pActivity) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pActivity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String email = sharedPreferences.getString("email", null);
                final String pin = sharedPreferences.getString("pin", null);
                try {
                    final String url = RegistrationIntentService.BASE_URL + "ping?email=" + URLEncoder.encode(email, "utf-8") + "&pin=" + URLEncoder.encode(pin, "utf-8");
                    Log.d("ping", url);
                    final String body = HttpRequest.get(url).body();
                    pActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(pActivity, "resp: " + body, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    pActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(pActivity, "error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
