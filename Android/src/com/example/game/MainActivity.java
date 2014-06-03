package com.example.game;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.game.network.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int MENU_RESUME = 1;
	private static final int MENU_START = 2;
	private static final int MENU_STOP = 3;
	private static final int MENU_MOTION = 4;
	
	public static ArrayList<Player> players = new ArrayList<Player>();

	private GameThread mGameThread;
	private GameView mGameView;

	private EditText username = null;
	private EditText password = null;
	private TextView attempts;
	private Button login;
	private EditText IP;
	int counter = 3;
	boolean loginScreen = true;

	static Client client;

	// final static String IP = "174.77.39.159"; // Robin's IP
	// final static String IP = "75.79.16.233"; // Val's IP
	// final static String IP = "169.234.18.145"; // uci mobile network on robin

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
		IP = (EditText) findViewById(R.id.editText3);

		client = new Client();
		Network.register(client.getEndPoint());
		client.addListener(new ClientListener());
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (!loginScreen && mGameThread.getMode() == GameThread.STATE_RUNNING) {
			mGameThread.setState(GameThread.STATE_PAUSE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		for(int i = 0; i < players.size(); i++){
//			Player player = players.get(i);
//			if(player.id == players.get(i).id){
//				players.remove(player);
//			}
//		}
		
		if (!loginScreen) {
			mGameView.cleanup();
			mGameView.removeSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
			mGameThread = null;
			mGameView = null;
		}
	}

	protected void onResume() {
		super.onResume();
		// new ConnectToServer().execute();
	}

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
			mGameThread.setState(GameThread.STATE_LOSE, getText(R.string.message_stopped));
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

	public class ConnectToServer extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				client.start();
				client.connect(5000, IP.getText().toString(), Network.PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public void login(View view) {
		// as long as username and password are not empty strings
		if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {

			LoginPacket packet = new LoginPacket();
			packet.username = username.getText().toString();
			packet.password = md5(password.getText().toString());

			new SendPacket().execute(packet);
		}
	}

	public void create(View view) {
	}

	public void connect(View view) {
		new ConnectToServer().execute();
	}

	private void startGame(GameView gView, GameThread gThread) {
		// Set up a new game, we don't care about previous states
		mGameThread = new ClientSwarch(mGameView);
		mGameView.setThread(mGameThread);
		mGameThread.setState(GameThread.STATE_READY);
		// hardcode username to the game thread
		mGameThread.username = username.getText().toString();
		mGameView.startSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
	}

	public class ClientListener extends Listener {
		public void connected(Connection connection) {
			Log.e("Kryonet", "connected");
		}

		public void received(Connection connection, Object object) {
			if (object instanceof StartPacket) {
				if (((StartPacket) object).start) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							loginScreen = false;
							setContentView(R.layout.activity_main);

							mGameView = (GameView) findViewById(R.id.gamearea);
							mGameView.setStatusView((TextView) findViewById(R.id.text));
							//mGameView.setScoreView((TextView) findViewById(R.id.score));

							startGame(mGameView, null);
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(), "Wrong Credentials",
									Toast.LENGTH_SHORT).show();
							attempts.setBackgroundColor(Color.RED);
							counter--;
							attempts.setText(Integer.toString(counter));
							if (counter == 0)
								login.setEnabled(false);
						}
					});
				}
			}
			else if(object instanceof CreatePlayerPacket){
				CreatePlayerPacket p = (CreatePlayerPacket) object;
				
				Player player = new Player(p.x, p.y, p.size);
				player.speed = p.speed;
				player.id = p.id;
				player.name = p.name;
				int color = players.size();
				if(color > 5){
					color = color % 5;
				}
				player.color = getColor(color);
				players.add(player);
				
			}
			else if(object instanceof PlayerPacket){
				PlayerPacket p = (PlayerPacket) object;
				for(Player player: players){
					if(player.id == p.id ){
						player.x = p.x;
						player.y = p.y;
						player.directionX = p.directionX;
						player.directionY = p.directionY;
						player.size = p.size;
						player.speed = p.speed;
						player.score = p.score;
					}
				}
			}
			else if(object instanceof PelletPacket){
				PelletPacket p = (PelletPacket) object;
				
				ClientSwarch.pellets.clear();
				int size = ClientSwarch.pelletSize;
				RectF p1 = new RectF(p.x1, p.y1, p.x1 + size, p.y1 + size);
				RectF p2 = new RectF(p.x2, p.y2, p.x2 + size, p.y2 + size);
				RectF p3 = new RectF(p.x3, p.y3, p.x3 + size, p.y3 + size);
				RectF p4 = new RectF(p.x4, p.y4, p.x4 + size, p.y4 + size);
				ClientSwarch.pellets.add(p1);
				ClientSwarch.pellets.add(p2);
				ClientSwarch.pellets.add(p3);
				ClientSwarch.pellets.add(p4);
			}
			
			else if(object instanceof DisconnectPacket){
				DisconnectPacket p = (DisconnectPacket) object;
				
				for(int i = 0; i < players.size(); i++){
					Player player = players.get(i);
					if(player.id == p.id){
						players.remove(player);
					}
				}
			}
		}
	}
	
	private int getColor(int color) {
		switch (color) {
		case 0:
			return Color.MAGENTA;
		case 1:
			return Color.BLUE;
		case 2:
			return Color.RED;
		case 3:
			return Color.YELLOW;
		case 4:
			return Color.GREEN;
		case 5:
			return Color.CYAN;

		}
		return Color.WHITE;
	}
}
