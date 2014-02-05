package com.python4d.monsi;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;

public final class Utils  {
	protected static final int REQUEST_GOOGLE_PLAY_SERVICES = 1;
	protected static final int SERVICE_MISSING =1;
	static MainActivity p;
	static String DEBUG_TAG = "MonSi";
	
	//constructeur pour récupérer l'activité principale
	Utils(MainActivity MainActivityParent){
		Utils.p=MainActivityParent;
	}
	
	// Gestion des sons du jeu Mediaplayer
	static void playSound(Context context, MediaPlayer mp,int resId) {
	    mp = MediaPlayer.create(context, resId);
	    //mp.setVolume(0.6f, 0.6f);
	    mp.start();
	    mp.setOnCompletionListener(new OnCompletionListener() {
	        public void onCompletion(MediaPlayer mp) {
	            mp.release();
	        };
	    });
	}
	// Gestion du volume
	static void streamVolume(int progress){
	AudioManager audioManager = 
		    (AudioManager)p.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	int audiomax=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	progress=(int)((float)audiomax*(float)progress/100.0);
	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
	}
	static boolean checkGooglePlayServicesAvailable() {
	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(p);
	    Log.i(DEBUG_TAG,"checkGooglePlayServicesAvailable, connectionStatusCode="+ connectionStatusCode);
	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
	    	if (connectionStatusCode==SERVICE_MISSING){
	    		showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);

		        return false;
	    	}
	    }
	    p.runOnUiThread(new Runnable() {
		    public void run() {
			    Toast t = Toast.makeText(p,"Google Play Service available", Toast.LENGTH_SHORT);
			    t.show();
		    }
	    });
	    return true;
	}
	static void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		p.runOnUiThread(new Runnable() {
		    public void run() {
		    final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
		        connectionStatusCode, p,
		        REQUEST_GOOGLE_PLAY_SERVICES);
		        if (dialog == null) {
		                Log.e(DEBUG_TAG,
		                        "couldn't get GooglePlayServicesUtil.getErrorDialog");
		                Toast.makeText(p,
		                        "Google Play Services Version incompatible...",
		                        Toast.LENGTH_LONG).show();
		            }
		            dialog.show();
		    }
		});
	}
}