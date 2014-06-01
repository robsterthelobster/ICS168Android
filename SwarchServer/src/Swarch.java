import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import network.*;

public class Swarch extends GameThread {

	public ArrayList<Rectangle> pellets = new ArrayList<Rectangle>();

	public int pelletSize = 1920 / 50;

	public final int width = 1920;
	public final int height = 1080;
	public final float SPEED = 1920 / 40;
	public final float SIZE = 1920 / 20;

	float timer = 0;

	public Swarch() {
		for (int i = 0; i < 4; i++) {
			pellets.add(addPellet(new Rectangle()));
		}
	}

	// This is run before a new game (also after an old game)
	@Override
	public void setupBeginning(boolean firstTimeSetUp) {
		for (int i = 0; i < 4; i++) {
			pellets.add(addPellet(new Rectangle(0, 0, 0, 0)));
		}
	}

	@Override
	protected void doDraw() {

	}

	// This is run just before the game "scenario" is printed on the screen
	@Override
	protected void updateGame(float secondsElapsed) {

//		for (int i = 0; i < players.size(); i++) {
//			players.get(i).x += players.get(i).directionX * players.get(i).speed * secondsElapsed;
//			players.get(i).y += players.get(i).directionY * players.get(i).speed * secondsElapsed;
//			//System.out.println(players.get(i).x + ", " + players.get(i).y);
//		}
		for(Player player: SwarchServer.players){
			player.x += player.directionX * player.speed * secondsElapsed;
			player.y += player.directionY * player.speed * secondsElapsed;
		}
		timer += secondsElapsed;
		if (timer >= 1) {
			timer = 0;
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

	private Rectangle addPellet(Rectangle rect) {
		int randX = randInt(pelletSize, width);
		int randY = randInt(pelletSize, height);

		rect.x = randX;
		rect.y = randY;
		rect.height = pelletSize;
		rect.width = pelletSize;

		return rect;
	}

	private int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

}