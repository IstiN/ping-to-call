package com.github.istin.pingtocall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.istin.pingtocall.service.HttpRequest;
import com.github.istin.pingtocall.service.RegistrationIntentService;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mInfoView.setText(intent.getAction() + "\n" + intent.getStringExtra("message"));
        }
    };

    private TextView mInfoView;
    private TextView mPhoneView;
    private TextView mEmailView;
    private TextView mPinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInfoView = (TextView) findViewById(R.id.info);
        mPhoneView = (TextView) findViewById(R.id.phone);
        mEmailView = (TextView) findViewById(R.id.email);
        mPinView = (TextView) findViewById(R.id.pin);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEmailView.setText(sharedPreferences.getString("email", null));
        mPinView.setText(sharedPreferences.getString("pin", null));
        mPhoneView.setText(sharedPreferences.getString("phone", null));
    }

    public void onSaveClick(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String phone = mPhoneView.getText().toString();
        sharedPreferences.edit()
                .putString("phone", phone)
                .putString("email", mEmailView.getText().toString())
                .putString("pin", mPinView.getText().toString()).apply();
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void onPingClick(View view) {
        RegistrationIntentService.ping(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE_ACTION);
        filter.addAction(RegistrationIntentService.REGISTRATION_ERROR_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}
