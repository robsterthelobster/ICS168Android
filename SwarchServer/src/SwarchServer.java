import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import network.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class SwarchServer extends Listener {

	static Server server;
	public static DatabaseManager dbm;
	public static ArrayList<Player> players = new ArrayList<Player>();
	static Swarch game;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		String dbName = "jdbc:sqlite:Swarch.db";

		dbm = new DatabaseManager(dbName);
		
		server = new Server();
		// register packets under here
		Network.register(server);
		server.bind(Network.PORT);
		server.start();

		server.addListener(new SwarchServer());

		System.out.println("Server started");
		
		System.out.println("game started");
		game = new Swarch();
		game.doStart();
		game.run();
		
	}

	public void connected(Connection c) {
		System.out.println("Connection received.");
	}

	public void received(Connection c, Object o) {

		//System.out.println("packet recieved");

		if (o instanceof LoginPacket) {
			LoginPacket packet = (LoginPacket) o;
			System.out.println("LOGIN");
			System.out.println("username: " + packet.username);
			System.out.println("password: " + packet.password);
			
			// send true to start
			// false to prompt wrong credentials
			StartPacket p = new StartPacket();
			// if login successful
			if(dbm.loginUser(packet.username, packet.password))
				p.start = true;
			else
				p.start = false;
			server.sendToTCP(c.getID(), p);
			
			if(p.start){
				
				for(Player player: players){
					CreatePlayerPacket cpp = new CreatePlayerPacket();
					cpp.size = player.size;
					cpp.speed = player.speed;
					cpp.x = player.x;
					cpp.y = player.y;
					cpp.id = player.id;
					cpp.name = player.name;
					server.sendToTCP(c.getID(), cpp);
				}
				
				CreatePlayerPacket cp = new CreatePlayerPacket();
				cp.size = game.SIZE;
				cp.speed = game.SPEED;
				cp.x = randInt(0, (int)(1920 - game.SIZE));
				cp.y = randInt(0, (int)(1080 - game.SIZE));
				cp.id = c.getID();
				cp.name = packet.username;
				
				server.sendToAllTCP(cp);
				
				Player player = new Player(cp.x, cp.y);
				player.x = randInt(0, (int)(1920 - game.SIZE));
				player.y = randInt(0, (int)(1080 - game.SIZE));
				player.id = c.getID();
				player.size = game.SIZE;
				player.speed = game.SPEED;
				player.name = packet.username;
				
				players.add(player);
				
				PelletPacket pp = new PelletPacket();
				pp.x1 = Swarch.pellets.get(0).x;
				pp.x2 = Swarch.pellets.get(1).x;
				pp.x3 = Swarch.pellets.get(2).x;
				pp.x4 = Swarch.pellets.get(3).x;
				pp.y1 = Swarch.pellets.get(0).y;
				pp.y2 = Swarch.pellets.get(1).y;
				pp.y3 = Swarch.pellets.get(2).y;
				pp.y4 = Swarch.pellets.get(3).y;
				
				server.sendToTCP(c.getID(), pp);
				
			}
		}
		if(o instanceof DirectionPacket){
			System.out.println("DIRECTION");
			DirectionPacket packet = (DirectionPacket) o;
			Player p = null;
			for(Player player: players){
				if(player.id == packet.id){
					p = player;
				}
			}
			
			p.directionX = packet.directionX;
			p.directionY = packet.directionY;
			System.out.println("ID: " + p.id + " , dirX: " + p.directionX
						 + " , dirY: " + p.directionY);
//			for (Player pl : SwarchServer.players) {
//				if(pl!=null){
					PlayerPacket cp = new PlayerPacket();
					cp.x = p.x;
					cp.y = p.y;
					cp.directionX = p.directionX;
					cp.directionY = p.directionY;
					cp.size = p.size;
					cp.speed = p.speed;
					cp.id = p.id;
					cp.score = p.score;
					SwarchServer.server.sendToAllTCP(cp);
//				}
//			}
		}
	}

	public void disconnected(Connection c) {
		System.out.println("client disconnected");
		DisconnectPacket p = new DisconnectPacket();
		p.id = c.getID();
		server.sendToAllExceptTCP(c.getID(), p);
		
		for(int i = 0; i < players.size(); i++){
			if(players.get(i) != null && players.get(i).id == c.getID()){
				players.remove(players.get(i));
			}
		}
	}
	
	private int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}
}
