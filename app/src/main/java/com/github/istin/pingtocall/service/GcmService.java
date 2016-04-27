/*
Copyright 2015 Google Inc. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.github.istin.pingtocall.service;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {

    public GcmService() {

    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification("Received GCM Message: " + data.toString());
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        Log.d("Gcm notification", msg);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        call(this, sharedPreferences.getString("phone", null));
    }

    private static void call(Context context, String phone) {
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + URLEncoder.encode(phone, "utf-8")));
        } catch (UnsupportedEncodingException pE) {
            pE.printStackTrace();
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Please install phone application", Toast.LENGTH_SHORT).show();
        }
    }
}