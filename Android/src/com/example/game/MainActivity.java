package com.example.game;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.game.network.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int MENU_RESUME = 1;
	private static final int MENU_START = 2;
	private static final int MENU_STOP = 3;
	private static final int MENU_MOTION = 4;

	private GameThread mGameThread;
	private GameView mGameView;

	private EditText username = null;
	private EditText password = null;
	private TextView attempts;
	private Button login;
	int counter = 3;
	boolean loginScreen = true;
	boolean start = false;

	Client client;
	final static String IP = "174.77.39.159"; // Robin's IP
	// final static String IP = "75.79.16.233"; // Val's IP
	//final static String IP = "169.234.18.145"; // uci mobile network on robin

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.login);
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		attempts = (TextView) findViewById(R.id.textView5);
		attempts.setText(Integer.toString(counter));
		login = (Button) findViewById(R.id.button1);

		client = new Client();
		Network.register(client.getEndPoint());
		client.addListener(new Listener() {
			public void connected(Connection connection) {
				Log.e("Kryonet", "connected");
			}
			
			public void received(Connection connection, Object object) {
				if(object instanceof StartPacket){
					if(((StartPacket) object).start)
						changeToGame();
				}
			}
		});

		new ConnectToServer().execute();
	}
	
	
	public void changeToGame(){
		this.runOnUiThread(new Runnable(){
			
			@Override
			public void run() {
				loginScreen = false;
				setContentView(R.layout.activity_main);
		
				mGameView = (GameView) findViewById(R.id.gamearea);
				mGameView.setStatusView((TextView) findViewById(R.id.text));
				mGameView.setScoreView((TextView) findViewById(R.id.score));
		
				startGame(mGameView, null);
			}
				
		});
	}

	public class ConnectToServer extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				client.start();
				client.connect(5000, IP, Network.PORT);

				// Toast.makeText(getApplicationContext(), packet.username + " "
				// + packet.password, Toast.LENGTH_SHORT).show();
				// System.out.println(packet.username + " " + packet.password);

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public class LoginToServer extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			LoginPacket packet = new LoginPacket();
			packet.username = username.getText().toString();
			packet.password = md5(password.getText().toString());
			client.sendTCP(packet);
			return null;
		}
	}

	public void login(View view) {
		// as long as username and password are not empty strings
		if (!username.getText().toString().equals("")
				&& !password.getText().toString().equals("")) {
			// new ConnectToServer().execute();

			// System.out.println(username.getText());
			new LoginToServer().execute();

			if (start) {
				loginScreen = false;
				setContentView(R.layout.activity_main);

				mGameView = (GameView) findViewById(R.id.gamearea);
				mGameView.setStatusView((TextView) findViewById(R.id.text));
				mGameView.setScoreView((TextView) findViewById(R.id.score));

				this.startGame(mGameView, null);
			}
		} else {
			Toast.makeText(getApplicationContext(), "Wrong Credentials",
					Toast.LENGTH_SHORT).show();
			attempts.setBackgroundColor(Color.RED);
			counter--;
			attempts.setText(Integer.toString(counter));
			if (counter == 0) {
				login.setEnabled(false);
			}
		}

	}

	private void startGame(GameView gView, GameThread gThread) {

		// Set up a new game, we don't care about previous states
		mGameThread = new Swarch(mGameView);
		mGameView.setThread(mGameThread);
		mGameThread.setState(GameThread.STATE_READY);
		// hardcode username to the game thread
		mGameThread.username = username.getText().toString();
		mGameView
				.startSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
	}

	/*
	 * Activity state functions
	 */

	@Override
	protected void onPause() {
		super.onPause();

		if (mGameThread.getMode() == GameThread.STATE_RUNNING && !loginScreen) {
			mGameThread.setState(GameThread.STATE_PAUSE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mGameView.cleanup();
		mGameView
				.removeSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
		mGameThread = null;
		mGameView = null;
	}

	protected void onResume() {
		super.onResume();
		// new ConnectToServer().execute();
	}

	/*
	 * UI Functions
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_START, 0, R.string.menu_start);
		menu.add(0, MENU_STOP, 0, R.string.menu_stop);
		menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
		menu.add(0, MENU_MOTION, 0, R.string.motion_enabled);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_START:
			mGameThread.doStart();
			return true;
		case MENU_STOP:
			mGameThread.setState(GameThread.STATE_LOSE,
					getText(R.string.message_stopped));
			return true;
		case MENU_RESUME:
			mGameThread.unpause();
			return true;
		case MENU_MOTION:
			mGameThread.setMotionControl();
			return true;
		}

		return false;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing if nothing is selected
	}

	// MD5 Converter (Credits to
	// http://stackoverflow.com/questions/3934331/android-how-to-encrypt-a-string)
	public static String md5(String s) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes(), 0, s.length());
			String hash = new BigInteger(1, digest.digest()).toString(16);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}

// This file is part of the course
// "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
//
// You should have received a copy of the GNU General Public License
// along with it. If not, see <http://www.gnu.org/licenses/>.
