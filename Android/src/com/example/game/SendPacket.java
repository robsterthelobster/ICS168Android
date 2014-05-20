package com.example.game;

import android.os.AsyncTask;
import com.example.game.network.*;

public class SendPacket extends AsyncTask<Packet, Void, Void> {

		@Override
		protected Void doInBackground(Packet... packet) {
			if (packet[0] instanceof LoginPacket) {
				LoginPacket p = (LoginPacket) packet[0];
				System.out.println(p.username);
				MainActivity.client.sendTCP(p);
			}
			if(packet[0] instanceof DirectionPacket){
				DirectionPacket p = (DirectionPacket) packet[0];
				System.out.println("direction packet");
				MainActivity.client.sendTCP(p);
			}
			return null;
		}
	}