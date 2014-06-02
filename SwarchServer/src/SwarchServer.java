import java.io.IOException;
import java.util.ArrayList;

import network.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class SwarchServer extends Listener {

	static Server server;
	private static DatabaseManager dbm;
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

		System.out.println("packet recieved");

		if (o instanceof LoginPacket) {
			LoginPacket packet = (LoginPacket) o;
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
					server.sendToTCP(c.getID(), cpp);
				}
				
				CreatePlayerPacket cp = new CreatePlayerPacket();
				cp.size = game.SIZE;
				cp.speed = game.SPEED;
				cp.x = players.size() * 200 + 100;
				cp.y = game.height/2;
				cp.id = c.getID();
				
				server.sendToAllTCP(cp);
				
				Player player = new Player(cp.x, cp.y);
				player.x = players.size() * 200 + 100;
				player.y = game.height/2;
				player.id = c.getID();
				player.size = game.SIZE;
				player.speed = game.SPEED;
				
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
			DirectionPacket packet = (DirectionPacket) o;
			for(Player player: players){
				if(player.id == packet.id){
					player.directionX = packet.directionX;
					player.directionY = packet.directionY;
					System.out.println("ID: " + player.id + " , dirX: " + player.directionX
								 + " , dirY: " + player.directionY);
				}
			}
			for (Player pl : SwarchServer.players) {
				if(pl!=null){
					PlayerPacket cp = new PlayerPacket();
					cp.x = pl.x;
					cp.y = pl.y;
					cp.directionX = pl.directionX;
					cp.directionY = pl.directionY;
					cp.size = pl.size;
					cp.speed = pl.speed;
					cp.id = pl.id;
					SwarchServer.server.sendToAllTCP(cp);
				}
			}
			
		}

	}

	public void disconnected(Connection c) {
		System.out.println("client disconnected");
		Player p = null;
		for(Player player: players){
			if(player.id == c.getID()){
				p = player;
		
			}
		}
		players.remove(p);
	}
}
