package com.python4d.monsi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class FTPConnect extends AsyncTask<Integer, Void, String[]> {

	MainActivity ma;
	AlertDialog.Builder Alert_getname=null;
	FTPConnect(MainActivity MainActivityParent) {
		this.ma = MainActivityParent;
	}

	protected String[] doInBackground(Integer... Params) {

		FTPClient client = new FTPClient();
		int buf = 1000;
		int highscore_local = Params[0];
		int level_local = Params[1];
		
		ma.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(ma, "Connexion Internet...",
						Toast.LENGTH_SHORT).show();
			}
		});
		try {
			client.connect("ftpperso.free.fr", 21);
			boolean b = client.login("forumpanhard", "baco22");
			client.setFileType(FTP.ASCII_FILE_TYPE);
			client.enterLocalPassiveMode();
			FTPFile[] dirs = client.mlistDir("/");
			FTPFile[] files = client.listFiles("/");
			String[] files2 = client.listNames("/");
			String filename = "test.php";
			ByteArrayOutputStream fis = new ByteArrayOutputStream(buf);
			b = client.retrieveFile(filename, fis);
			ma.hs_web = fis.toString().split("\n");
			ma.hs_web[0] = ma.hs_web[0].split("=")[1];
			ma.hs_web[1] = ma.hs_web[1].split("=")[1];
			ma.hs_web[2] = ma.hs_web[2].split("=")[1];
			if (ma.highscore[0] > Integer.parseInt(ma.hs_web[0])) {
				ma.hs_name = "-";
				ma.runOnUiThread(new Runnable() {
					public void run() {
						//Préparer la boite d'Alert pour récupérer le nom du champion (il suffit de faire .show() pour lancer la boite)
				        final EditText name_EditText = new EditText(ma);
				        name_EditText.setInputType(
				        		InputType.TYPE_CLASS_TEXT|
				        		InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|
				        		InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
				        name_EditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
				        Alert_getname=new AlertDialog.Builder(ma)
				        .setTitle("All-Time HighScore !")
				        .setMessage("Bravo!\n Nouveau record du Web ! \nEntre tes initiales:")
				        .setView(name_EditText)
				        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int whichButton) {
				                String value = name_EditText.getText().toString(); 
				                ma.hs_name=value;
				            }
				        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int whichButton) {
				            	ma.hs_name="?";
				            }
				        });
						Alert_getname.show();
					}
				});

				while (ma.hs_name == "-") {
				}
				ma.hs_web[0] = Integer.toString(ma.highscore[0]);
				ma.hs_web[1] = Integer.toString(ma.highscore[1]);
				if (ma.hs_name.equals("") || ma.hs_name.equals("?")) {// Cas cancel ou le nom est vide
					ma.hs_name = "BaCo";
					ma.hs_web[2] = "BaCo";
				} else {
					ma.hs_web[2] = new String(ma.hs_name);

				}

				String maChaine = new String("hs="
						+ Integer.toString(ma.highscore[0]) + "\nlevel="
						+ Integer.toString(ma.highscore[1]) + "\nname="
						+ ma.hs_name + "\n");
				InputStream in = new ByteArrayInputStream(maChaine.getBytes());
				b = client.storeFile(filename, in);
			}
			client.logout();
			ma.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(ma,
							"All Time HighScore\nSuccès de la connexion !",
							Toast.LENGTH_SHORT).show();
					ma.textHighScoreWeb.setText("All-Time HighScore="
							+ ma.hs_web[0] + " (Level " + ma.hs_web[1]
							+ ") by '" + ma.hs_web[2] + "'");
				}
			});
		} catch (IOException e) {
			ma.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(ma, "Echec Connexion...", Toast.LENGTH_SHORT)
							.show();
					ma.textHighScoreWeb.setText("All-Time HighScore="
							+ ma.hs_web[0] + " (Level " + ma.hs_web[1]
							+ ") by '" + ma.hs_web[2] + "'");
				}
			});
			e.printStackTrace();
		}
		return ma.hs_web;
	}

}
