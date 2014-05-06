package com.example.testclient;

import java.io.IOException;

import android.os.AsyncTask;


public class ConnectToServer extends AsyncTask<String, int[], String>{

	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		try{
			Network.client.connect(5000, Network.ip, Network.port);
		}
		catch (IOException e)
		{
			
			e.printStackTrace();
		}
		return null;
	}

}
