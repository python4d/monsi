package com.python4d.monsi;

import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.animation.Animation;

public final class StateMachine {
	MainActivity p;

	StateMachine(MainActivity MainActivityParent){
		this.p=MainActivityParent;
	}
	
	Runnable updateTimerThread = new Runnable() {
		MediaPlayer mpHurryUp=null;
		int state=10;
		int verif,flag_time = 0;
		int level = 1;
		int nb_boutons = 2;
		int score,time = 0;
		final String[] sTextesIntro = { "3", "2", "1", "Go !"};
		int[] sSuite={0,0,0,0,0,0,0,0,0,0};
		int i_animation_intro = 0;
		long lCountms = 0;
		Random r = new Random();
		@Override
		public void run() {
			if (p.bPause){
				p.customHandler.postDelayed(updateTimerThread, 1000);
			}else{
	
				switch (state)
				{
				case 10 : 
					mpHurryUp=MediaPlayer.create((Context)p, R.raw.hurry_up);
					for (int i = 0; i < 9; i++) {
						p.boutonsNomis[i].setOnClickListener(null); 
						p.boutonsNomis[i].setText("");
					}
					p.textIntro.setVisibility(0);
					p.boutonStart.setClickable(false);
					i_animation_intro = 0;
					p.textLevel.setText("Level=" + Integer.toString(level));
					p.textScore.setText("Score=" + Integer.toString(score));
					p.textTime.setText("Time=" + Integer.toString(time));
					p.textIntro.setText(sTextesIntro[i_animation_intro]);
					p.textIntro.startAnimation(p.animTextIntro);	
					p.animTextIntro.setDuration(100);
					state=20;
					p.customHandler.postDelayed(updateTimerThread, 10);
					break;
					
				case 20://animation
					if (p.animTextIntro.hasEnded()){
						i_animation_intro++;
						if (i_animation_intro>3){
							p.textIntro.setText("");
							state=30;
						}
						else{
							p.textIntro.setText(sTextesIntro[i_animation_intro]);
							p.textIntro.startAnimation(p.animTextIntro);	
						}
					}
					p.customHandler.postDelayed(updateTimerThread, 100);
					break;	
				case 30://Animation Level
					p.textIntro.setText("Level "+Integer.toString(level));
					p.animTextIntro.setDuration(500);
					p.textIntro.startAnimation(p.animTextIntro);	
					state = 32;
					p.customHandler.postDelayed(updateTimerThread, 500);
					break;
				case 32://on montre les jetons
	
					for (int i = 1; i <= nb_boutons; i++) {
						int rr = r.nextInt(9);
						if (p.boutonsNomis[rr].getText() == "") {
							sSuite[i-1]=rr;
							p.boutonsNomis[rr].setText(Integer.toString(i));
						} 
						else
							i--;
					}
					state = 35;
					p.customHandler.postDelayed(updateTimerThread, 1000);
					p.fFirstPush=0;
					p.sBoutonTouche="";
					lCountms=SystemClock.uptimeMillis ();
					for (int i = 0; i < 9; i++) {
						p.boutonsNomis[i].setOnClickListener(p.firstboutonNomisListener);
					}				
					break;
				case 35://on attends un certain temps  ou l'appui d'une touche
					
					if (p.fFirstPush==0 && (int) (SystemClock.uptimeMillis ()-lCountms)<nb_boutons*1500-level*100 ){
					}
	
					else{
						state=40;
					}
					p.customHandler.postDelayed(updateTimerThread, 10);
					break;
	
				case 40://on cache les jetons et on calcul le temps
					lCountms=SystemClock.uptimeMillis ();
					for (int i = 0; i < 9; i++) {
						p.boutonsNomis[i].setOnClickListener(p.boutonNomisListener);
						p.boutonsNomis[i].setText("");
					}
					verif=0;
					state=50;
					p.customHandler.postDelayed(updateTimerThread, 10);
					break;
				case 50://on attends une sélection de jeton/bouton
					time=nb_boutons*1000-level*100-(int) (SystemClock.uptimeMillis ()-lCountms);
					time=(time>0)?time:0;
					p.textTime.setText("Time=" + Integer.toString(time));
					if (time<2450 && flag_time==0){
						flag_time=1;
						mpHurryUp.start();
					}
					if (time==0){					
						flag_time=0;
						if (score>=p.highscore[0]){
							p.highscore[0]=score;p.highscore[1]=level;
							p.textHighScore.setText("Local HighScore ="+Integer.toString(p.highscore[0])+" (Level "+Integer.toString(p.highscore[1])+")");	
					        p.editor.putInt("highscore", p.highscore[0]);
							p.editor.putInt("level", p.highscore[1]);
							p.editor.commit();
							//ftp: update & get/put highscore from web
							if(p.ftp.getStatus() == AsyncTask.Status.PENDING){
							    // My AsyncTask has not started yet
							}
		
							if(p.ftp.getStatus() == AsyncTask.Status.RUNNING){
							    // My AsyncTask is currently doing work in doInBackground()
							}
		
							if(p.ftp.getStatus() == AsyncTask.Status.FINISHED){
							    // START NEW TASK HERE
							}
							p.ftp=null;
					        p.ftp=(FTPConnect) new FTPConnect(p).execute( p.highscore[0],p.highscore[1]);
						}

						p.textIntro.setText("Game Over");
						p.animTextIntro.setDuration(3000);
						p.animTextIntro.setRepeatCount(5);
						p.animTextIntro.setRepeatMode(Animation.REVERSE);
						p.textIntro.startAnimation(p.animTextIntro);	
						//Utils.playSound((Context) p, p.mPlayer, R.raw.pacman_death);
						Utils.playSound(Utils.sound_pacman_death);
						
						for (int i = 0; i < 9; i++) {
							p.boutonsNomis[i].setOnClickListener(null);
							p.boutonsNomis[i].setText(Integer.toString(i+1));
						}
						p.boutonStart.setClickable(true);
						break;
					}
					if (p.sBoutonTouche!=""){
						if (sSuite[verif]==Integer.parseInt(p.sBoutonTouche)){
							//Utils.playSound((Context) p, p.mPlayer, R.raw.cerise);
							Utils.playSound(Utils.sound_cerise,0.5f);
							p.boutonsNomis[sSuite[verif]].setText(Integer.toString(verif+1));
							score+=++verif+(int)((float)time/10.0);
							p.textScore.setText("Score=" + Integer.toString(score));	
						}
						else
						{
							//Utils.playSound((Context) p, p.mPlayer,  R.raw.ungh);
							Utils.playSound(Utils.sound_ungh,0.2f);
							
						}
						if (verif==nb_boutons){			
							
							if (flag_time==1) {
								mpHurryUp.pause();
								mpHurryUp.seekTo(0);
								flag_time=0;
							}
							level++;
							if (nb_boutons<9) nb_boutons++;
							p.textLevel.setText("Level=" + Integer.toString(level));
							lCountms=SystemClock.uptimeMillis ();
							for (int i = 1; i <=nb_boutons; i++) { 
								p.boutonsNomis[sSuite[i-1]].setText("");
							}
							for (int i = 0; i < 9; i++) {
								p.boutonsNomis[i].setOnClickListener(null);
								//p.boutonsNomis[i].setText("");
							}
							state = 30;
						}
						p.sBoutonTouche="";
					}
					else
					{
	
					}
					p.customHandler.postDelayed(updateTimerThread, 10);
					break;
				case 999:

					//ftp: update & get/put highscore from web
					if(p.ftp.getStatus() == AsyncTask.Status.PENDING){
					    // My AsyncTask has not started yet
					}

					if(p.ftp.getStatus() == AsyncTask.Status.RUNNING){
					    // My AsyncTask is currently doing work in doInBackground()
					}

					if(p.ftp.getStatus() == AsyncTask.Status.FINISHED){
					    // START NEW TASK HERE
					}
					p.ftp=null;
					p.runOnUiThread(new Runnable() {
						public void run() {
							p.ftp=(FTPConnect) new FTPConnect(p).execute( p.highscore[0],p.highscore[1]);
						}
					});
			        break;
				}
			}
		}
	};
}