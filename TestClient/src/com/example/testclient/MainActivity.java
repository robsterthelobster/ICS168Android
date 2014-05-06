package com.example.testclient;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Network network = new Network();
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				new ConnectToServer().execute();
			}
		});
		
		// network.connect();
		// new ConnectToServer().execute();

		
//		try {
//			client.connect(5000, "10.0.2.2", 8080);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public class ConnectToServer extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Client client;
				client = new Client();
				client.addListener(new Listener() {
					public void connected(Connection connection) {
						Log.e("Kryonet", "connected");
					}
				});
				client.start();
				client.connect(5000, "174.77.39.159", 8080);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
