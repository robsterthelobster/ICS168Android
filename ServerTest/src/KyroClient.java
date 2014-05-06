import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class KyroClient extends Listener{
	Client client;
	//String ip = "174.77.39.159";
	String ip = "localhost";
	//String ip = "172.0.0.1";
	int port = 4444;
	
	//public KyroClient(){};
	
	public void connect(){
		client = new Client();
		client.addListener(this);
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
	
	public static void main(String args[])
	{
		KyroClient c = new KyroClient();
		c.connect();
	}
}
