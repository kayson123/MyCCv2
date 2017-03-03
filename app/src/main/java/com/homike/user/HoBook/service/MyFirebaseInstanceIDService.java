package com.homike.user.HoBook.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.homike.user.HoBook.app.Config;

/**
 * Created by User on 2/27/2017.
 */

//this class receives firebase registration ID which will be unique to each app
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Saving reg id to shared pref
        storeRegIdInPref(refreshedToken);
        //Sending reg id to server
        sendRegistrationToServer(refreshedToken);
        //Notify UI that registration is completed so the progress indicator can be hidden
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token",refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token){
        //sending GCM  token to server
        Log.e(TAG,"send RegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
