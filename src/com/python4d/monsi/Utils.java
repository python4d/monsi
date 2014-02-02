package com.python4d.monsi;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public final class Utils{
	static void playSound(Context context, MediaPlayer mp,int resId) {
	    mp = MediaPlayer.create(context, resId);
	    mp.setVolume(0.6f, 0.6f);
	    mp.start();
	    mp.setOnCompletionListener(new OnCompletionListener() {
	        public void onCompletion(MediaPlayer mp) {
	            mp.release();
	        };
	    });
	}
}