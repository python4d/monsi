package com.python4d.monsi;
import com.google.android.gms.ads.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.python4d.monsi.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	final int RQS_GooglePlayServices = 1;
	static boolean pub=true;
	MediaPlayer mPlayer=null;
	boolean bPause=false;
	private Animation animBoutonRetourne;
	Animation animTextIntro = null;
	Button boutonsNomis[] = null;
	Button boutonStart = null;
	TextView textLevel, textScore,textTime, textIntro,textHighScore,textHighScoreWeb,textWebPython= null;
	String sBoutonTouche=null;
	int fFirstPush=0;
	int highscore[]={0,0};
	String[] hs_web={"------","--","-----"};
	SharedPreferences.Editor editor = null;
	SharedPreferences preferences = null;
	Handler customHandler = new Handler();
	MainActivity ma=this;
	String hs_name=null;
	FTPConnect ftp=null;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        

	    
		//mettre en plein écran
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //récupérer le highscore local du téléphone
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        highscore[0]=preferences.getInt("highscore", 0);
        highscore[1]=preferences.getInt("level", 0);
        
        //ftp: update & get highscore from web
        ftp=(FTPConnect) new FTPConnect(this).execute( highscore[0],highscore[1]);
        
		setContentView(R.layout.activity_main);
		// Check status of Google Play Services
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		// Check Google Play Service Available
		try {
		    if (status != ConnectionResult.SUCCESS) {
		        GooglePlayServicesUtil.getErrorDialog(status, this, RQS_GooglePlayServices).show();
		    }
		} catch (Exception e) {
		    Log.e("Error: GooglePlayServiceUtil: ", "" + e);
		}
		//ads
		// Look up the AdView as a resource and load a request.
		
		if (pub) {
			AdView adView = (AdView)this.findViewById(R.id.adView);
		    AdRequest adRequest = new AdRequest.Builder().build();
		    adView.loadAd(adRequest);
		}
	    
		textHighScore = (TextView) findViewById(R.id.textHighScore);
		textScore = (TextView) findViewById(R.id.textScore);
		textHighScoreWeb = (TextView) findViewById(R.id.textHighScoreWeb);
		textTime = (TextView) findViewById(R.id.textTime);
		textLevel = (TextView) findViewById(R.id.textLevel);
		textIntro = (TextView) findViewById(R.id.textIntro);
		textWebPython = (TextView) findViewById(R.id.textWebPython);
		textWebPython.setOnClickListener(textWebPythonListener);
		boutonStart = (Button) findViewById(R.id.buttonStart);
		boutonStart.setOnClickListener(boutonStartListener);

		boutonsNomis = new Button[9];
		boutonsNomis[0] = (Button) findViewById(R.id.Button00);
		boutonsNomis[1] = (Button) findViewById(R.id.Button01);
		boutonsNomis[2] = (Button) findViewById(R.id.Button02);
		boutonsNomis[3] = (Button) findViewById(R.id.Button03);
		boutonsNomis[4] = (Button) findViewById(R.id.Button04);
		boutonsNomis[5] = (Button) findViewById(R.id.Button05);
		boutonsNomis[6] = (Button) findViewById(R.id.Button06);
		boutonsNomis[7] = (Button) findViewById(R.id.Button07);
		boutonsNomis[8] = (Button) findViewById(R.id.Button08);

		AnimationUtils.loadAnimation(this, R.anim.anim_bouton);
		animBoutonRetourne = AnimationUtils.loadAnimation(this, R.anim.anim_bouton_retourne);
		animTextIntro = AnimationUtils.loadAnimation(this, R.anim.anim_textintro);
		animTextIntro.setAnimationListener(textIntroListener);
		
		
		textHighScore.setText("Local HighScore ="+Integer.toString(highscore[0])+" (Level "+Integer.toString(highscore[1])+")");	
	}

	OnClickListener textWebPythonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Utils.playSound((Context) MainActivity.this, mPlayer, R.raw.pacman_death);
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.Python4D.com"));
			startActivity(i); 
		}
	};
	OnClickListener boutonNomisListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			v.startAnimation(animBoutonRetourne);
			sBoutonTouche=getResources().getResourceEntryName(v.getId());
			sBoutonTouche=sBoutonTouche.substring(sBoutonTouche.length()-1);
		}
	};
	OnClickListener firstboutonNomisListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			fFirstPush=1;
			for (int i = 0; i < 9; i++) {
				boutonsNomis[i].setOnClickListener(boutonNomisListener);
			}
			v.performClick();
		}
	};

	private OnClickListener boutonStartListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Utils.playSound((Context) MainActivity.this, mPlayer, R.raw.up);
			customHandler.postDelayed((new StateMachine(MainActivity.this)).updateTimerThread, 100);
		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private AnimationListener textIntroListener = new AnimationListener() {
		public void onAnimationEnd(Animation arg0) {

		}

		public void onAnimationRepeat(Animation arg0) {
		}

		public void onAnimationStart(Animation arg0) {

		}

	};
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    bPause=true;
	}
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    bPause=false;
	}
	@Override
    public void onDestroy()
    {
        super.onDestroy();
		ma.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(ma, "Simon, le frère de MonSi te dit au revoir !",
						Toast.LENGTH_LONG).show();
			}
		});
    }
	@Override
    public void onStop()
    {
        super.onStop();
	    bPause=true;
 
    }
}
