package com.python4d.monsi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	static boolean pub=true;
	MediaPlayer mPlayer=null;
	boolean bPause=false;
	private Animation animBoutonRetourne;
	SeekBar seekbarVolume = null;
	Animation animTextIntro = null;
	Button boutonsNomis[] = null;
	Button boutonStart = null;
	TextView textLevel,textScore,textTime, textIntro,textHighScore,textHighScoreWeb,textWebPython,textVolume= null;
	String sBoutonTouche=null;
	int fFirstPush=0;
	int highscore[]={0,0};
	String[] hs_web={"------","--","-----"};
	SharedPreferences.Editor editor = null;
	SharedPreferences preferences = null;
	Handler customHandler = new Handler();
	String hs_name=null;
	FTPConnect ftp=null;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        

	    //Fonctions static de la class Utils associé à la classe MainActivity (permet d'alléger le fichier MainActivity.java)
		new Utils(this);
		//mettre en plein écran
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // change the music vol instead of ringtone vol
        // when hardware volume buttons are pressed
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //récupérer la view global et mettre le son par defaut des boutons
		LayoutInflater inflater = getLayoutInflater();
		View l = inflater.inflate(R.layout.activity_main,null);
		l.setSoundEffectsEnabled(true);
        
        
        //récupérer le highscore local du téléphone
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        highscore[0]=preferences.getInt("highscore", 0);
        highscore[1]=preferences.getInt("level", 0);
        
        //ftp: update & get highscore from web
        ftp=(FTPConnect) new FTPConnect(this).execute( highscore[0],highscore[1]);
        
		setContentView(R.layout.activity_main);
		
		try{
			if (pub) {
				AdView adView = (AdView)this.findViewById(R.id.adView);
			    AdRequest adRequest = new AdRequest.Builder().build();
			    adView.loadAd(adRequest);
			}
		} catch (Throwable  e) {
		    Log.e("Error: GooglePlayServiceUtil: ", "" + e);
		}
		//ads
		// Look up the AdView as a resource and load a request.
		
		seekbarVolume = (SeekBar)findViewById(R.id.seekBarVolume); // make seekbar object
		seekbarVolume.setOnSeekBarChangeListener(seekbarVolumeListener); // set seekbar listener.
		seekbarVolume.setProgress(50);
		Utils.streamVolume(50);				
		seekbarVolume.invalidate();

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
		textVolume= (TextView) findViewById(R.id.textVolume);
		textVolume.setOnClickListener(textVolumeListener);

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
		//désactivez le son des 9 boutons 
		for (Button i:boutonsNomis)
			i.setSoundEffectsEnabled(false);

		AnimationUtils.loadAnimation(this, R.anim.anim_bouton);
		animBoutonRetourne = AnimationUtils.loadAnimation(this, R.anim.anim_bouton_retourne);
		animTextIntro = AnimationUtils.loadAnimation(this, R.anim.anim_textintro);
		animTextIntro.setAnimationListener(textIntroListener);
	
		textHighScore.setText("Local HighScore ="+Integer.toString(highscore[0])+" (Level "+Integer.toString(highscore[1])+")");	
	}

	OnClickListener textVolumeListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			
			if (textVolume.getTypeface()==Typeface.DEFAULT_BOLD){
				Utils.streamVolume(seekbarVolume.getProgress());				
				textVolume.setTypeface(Typeface.DEFAULT);
				textVolume.setTextColor(getResources().getColor(R.color.black));
				textVolume.setText(((String)textVolume.getText()).replace("OFF", "ON"));
			}
			else{
				Utils.streamVolume(0);			
				//TIP =>  yourbutton.setSoundEffectsEnabled(false); TODO:for disale click of button
				textVolume.setTypeface(Typeface.DEFAULT_BOLD);
				textVolume.setTextColor(getResources().getColor(R.color.red));
				textVolume.setText(((String)textVolume.getText()).replace("ON", "OFF"));
			}

		}
	};
	OnSeekBarChangeListener seekbarVolumeListener = new OnSeekBarChangeListener (){
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			if (textVolume.getTypeface()!=Typeface.DEFAULT_BOLD){
				Utils.streamVolume(progress);
			}
	    }

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
	};
	OnClickListener textWebPythonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Utils.playSound(Utils.sound_pacman_death);
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
			//Utils.playSound((Context) MainActivity.this, mPlayer, R.raw.up);
			Utils.playSound(Utils.sound_up);
			customHandler.postDelayed((new StateMachine(MainActivity.this)).updateTimerThread, 100);
		};
	};


	private AnimationListener textIntroListener = new AnimationListener() {
		public void onAnimationEnd(Animation arg0) {

		}

		public void onAnimationRepeat(Animation arg0) {
		}

		public void onAnimationStart(Animation arg0) {

		}

	};
	
	// Gestion de la vie l'activité (cf onCreate)
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    bPause=true;
	}
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    bPause=false;
	    try{
		// Check Google Play Service Available
	    	Utils.checkGooglePlayServicesAvailable();
		} catch (Throwable  e) {
		    Log.e("Error in OnResume during 'checkGooglePlayServicesAvailable': GooglePlayServiceUtil: ", "" + e);
		}
	}
	@Override
    public void onDestroy()
    {
        super.onDestroy();
		Toast.makeText(this, "Simon, le frère de MonSi te dit au revoir !",
		Toast.LENGTH_LONG).show();
    }
	@Override
    public void onStop()
    {
        super.onStop();
	    bPause=true;
 
    }
	
	
	// Gestion des boutons de Volume pour être en cohérent avec la barre de volume seekbar 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_VOLUME_UP:
    		seekbarVolume.setProgress(seekbarVolume.getProgress()+7);	
    		if (textVolume.getTypeface()!=Typeface.DEFAULT_BOLD){
    			return super.onKeyDown(keyCode, event);
    		}
	    	return true;
	    case KeyEvent.KEYCODE_VOLUME_DOWN:
    		seekbarVolume.setProgress(seekbarVolume.getProgress()-7);	
    		if (textVolume.getTypeface()!=Typeface.DEFAULT_BOLD){
    			return super.onKeyDown(keyCode, event);
    		}
	    	return true;
	    default:
	    	//ne pas suivre la doc qui propose "return false"... 
	    	//mais cela ne marche pas avec LeaveButtonn ik faut utiliser la méthode parent
	        return super.onKeyDown(keyCode, event); 
	    }
	}
	// Gestion du Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void Aide(MenuItem item){
		AlertDialog.Builder alertAide=null;
		bPause=true;
		alertAide = new AlertDialog.Builder(this);
		alertAide.setTitle("Aide - Règles du Jeu")
		.setMessage(getString(R.string.aide))
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				bPause=false;
			}
		});		
		TextView textView = (TextView) alertAide.show().findViewById(android.R.id.message);
	    textView.setTextSize(14);
    }
}
