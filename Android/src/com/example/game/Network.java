package com.example.game;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


// listens to server and receives packets
// send packets in main thread
public class Network extends Listener {

	static Client client;
	//static String ip = "174.77.39.159";
	static String ip = "localhost";
	//String ip = "172.0.0.1";
	static int port = 4444;
	
	public void connect(){
		client = new Client();
		client.addListener(this);
		client.getKryo().register(Packet.class);
		client.start();
		System.out.println("Connecting");
		try {
			// maximum of 5000ms blocks
			client.connect(5000, ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void received(Connection c, Object o){
		System.out.println("Received");
	}
}
