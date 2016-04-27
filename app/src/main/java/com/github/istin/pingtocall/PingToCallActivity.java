package com.github.istin.pingtocall;

import android.app.Activity;
import android.os.Bundle;

import com.github.istin.pingtocall.service.RegistrationIntentService;

public class PingToCallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegistrationIntentService.ping(this);
        finish();
    }
}
