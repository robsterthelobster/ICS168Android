import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import network.PlayerPacket;


public class Player {
	
	public int id;
	public float x;
	public float y;
	public int directionX;
	public int directionY;
	public float size;
	public float speed;
	public int score;
	public String name;
	
	private float startX, startY;
	
	public Player(float x, float y){
		startX = x;
		startY = y;
	}
	
	public void update(float secondsElapsed){
		x += directionX * speed * secondsElapsed;
		y += directionY * speed * secondsElapsed;
		
		if(x < 0 || x + size > 1920 || y < 0 || y + size > 1080){
			reset();
			updateClients();
		}
		
		for(Pellet pellet : Swarch.pellets){
			if(pellet.intersects(x, y, size, size)){
				
				size *= 1.1;
				speed *= 0.9;
				score++;
				pellet.update();
				updateClients();
			}
		}
		
		for(int i = 0; i < SwarchServer.players.size(); i++){
			Player player = SwarchServer.players.get(i);
			if(player != null && player != this){
				Rectangle rect = new Rectangle();
				rect.setRect(player.x, player.y, player.size, player.size);
				if(rect.intersects(x, y, size, size)){
					if(player.size > size){
						player.increase();
						reset();
					}
					else if(player.size < size){
						increase();
						player.reset();
					}
					else{
						reset();
						player.reset();
					}
					updateClients();
				}
			}
		}
	}
	
	public void reset(){
		SwarchServer.dbm.updateScore(name, score);
		size = Swarch.SIZE;
		
		x = randInt(0, (int)(1920 - 3*size));
		y = randInt(0, (int)(1080 - 3*size));
		
		speed = Swarch.SPEED;
		score = 0;
	}
	
	public void increase(){
		size *= 1.1;
		speed *= 0.9;
		score += 10;
	}
	
	public void updateClients(){
		for (Player pl : SwarchServer.players) {
			PlayerPacket cp = new PlayerPacket();
			cp.x = pl.x;
			cp.y = pl.y;
			cp.directionX = pl.directionX;
			cp.directionY = pl.directionY;
			cp.size = pl.size;
			cp.speed = pl.speed;
			cp.id = pl.id;
			cp.score = pl.score;
			SwarchServer.server.sendToAllTCP(cp);
		}
	}
	
	private int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}
}
