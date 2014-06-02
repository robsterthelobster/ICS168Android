import java.awt.Rectangle;

import network.PlayerPacket;


public class Player {
	
	public int id;
	public float x;
	public float y;
	public int directionX;
	public int directionY;
	public float size;
	public float speed;
	
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
		}
		
		for(Pellet pellet : Swarch.pellets){
			if(pellet.intersects(x, y, size, size)){
				
				size *= 1.1;
				speed *= 0.9;
				pellet.update();
				for (Player pl : SwarchServer.players) {
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
		
		for(Player player : SwarchServer.players){
			if(player != null && player != this){
				Rectangle rect = new Rectangle();
				rect.setRect(player.x, player.y, player.size, player.size);
				if(rect.intersects(x, y, size, size)){
					if(rect.width > size){
						player.increase();
						reset();
					}
					else if(rect.width < size){
						increase();
						player.reset();
					}
					else{
						reset();
						player.reset();
					}
					for (Player pl : SwarchServer.players) {
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
	}
	
	public void reset(){
		x = startX;
		y = startY;
		size = Swarch.SIZE;
		speed = Swarch.SPEED;
	}
	
	public void increase(){
		size *= 1.5;
		speed *= 0.9;
	}
	
}
