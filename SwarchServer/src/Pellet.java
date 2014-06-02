import java.awt.Rectangle;
import java.util.Random;

import network.PelletPacket;


@SuppressWarnings("serial")
public class Pellet extends Rectangle{

	public void update(){
		this.x = randInt(0, 1920);
		this.y = randInt(0, 1080);
		
		PelletPacket pp = new PelletPacket();
		pp.x1 = Swarch.pellets.get(0).x;
		pp.x2 = Swarch.pellets.get(1).x;
		pp.x3 = Swarch.pellets.get(2).x;
		pp.x4 = Swarch.pellets.get(3).x;
		pp.y1 = Swarch.pellets.get(0).y;
		pp.y2 = Swarch.pellets.get(1).y;
		pp.y3 = Swarch.pellets.get(2).y;
		pp.y4 = Swarch.pellets.get(3).y;
		
		SwarchServer.server.sendToAllTCP(pp);
	}
	
	private int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}
}
