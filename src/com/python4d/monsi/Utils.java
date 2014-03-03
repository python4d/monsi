package com.python4d.monsi;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;

public final class Utils  {
	protected static final int REQUEST_GOOGLE_PLAY_SERVICES = 1;
	protected static final int SERVICE_MISSING =1;
	static MainActivity p;
	static String DEBUG_TAG = "MonSi";
	private static SoundPool sp;
	protected static int sound_up,sound_cerise,sound_hurry_up,sound_pacman_death,sound_ungh,sound_coin;
	
	//constructeur pour récupérer l'activité principale et affecter les sons au SoundPool
	Utils(MainActivity MainActivityParent){
		Utils.p=MainActivityParent;
		Utils.sp= new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		sound_cerise=Utils.sp.load(p, R.raw.cerise,0);
		sound_up=Utils.sp.load(p, R.raw.up,0);
		sound_hurry_up=Utils.sp.load(p, R.raw.hurry_up,0);
		sound_pacman_death=Utils.sp.load(p, R.raw.pacman_death,0);
		sound_coin=Utils.sp.load(p, R.raw.coin,0);
		sound_ungh=Utils.sp.load(p, R.raw.ungh,0);
		
	}
	

	// Gestion des sons du jeu avec SoundPool
	static void playSound(int soundID) {
		sp.play(soundID, 1, 1, 0, 0, 1);
	}
	static void playSound(int soundID,float volume) {
		sp.play(soundID, volume, volume, 0, 0, 1);
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