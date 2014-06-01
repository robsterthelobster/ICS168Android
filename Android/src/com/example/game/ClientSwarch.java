package com.example.game;

import java.util.ArrayList;
import java.util.Random;
import com.example.game.network.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

public class ClientSwarch extends GameThread {

	private ArrayList<RectF> pellets;
	private int pelletSize;

	private float xPress, yPress, xRelease, yRelease;

	private static final int SWIPE_MIN_DISTANCE = 120;

	// This is run before anything else, so we can prepare things here
	public ClientSwarch(GameView gameView) {
		// House keeping
		super(gameView);

		// just declaring stuff here, values are basically ignored
		// look at setUpBeginning for new games values
		paint = new Paint();
		pellets = new ArrayList<RectF>();
	}

	// This is run before a new game (also after an old game)
	@Override
	public void setupBeginning(boolean firstTimeSetUp) {

		// System.out.println("Height: " + mCanvasHeight);
		pelletSize = mCanvasHeight / 50;

		setScore(0);

		if (firstTimeSetUp)
			for (int i = 0; i < 4; i++)
				pellets.add(addPellet(new RectF()));
	}

	@Override
	protected void doDraw(Canvas canvas) {
		// If there isn't a canvas to do nothing
		// It is ok not understanding what is happening here
		if (canvas == null)
			return;

		// House keeping
		// this only draws the background, we can change it when deemed
		// necessary
		super.doDraw(canvas);

		// draw pellets as white
		paint.setColor(Color.WHITE);
		for (RectF rect : pellets) {
			canvas.drawRect(rect, paint);
		}

		// draw player as blue
		paint.setColor(Color.BLUE);
		for (Player player : MainActivity.players) {
			canvas.drawRect(player.rect, paint);
		}

	}

	// This is run whenever the phone is touched by the user
	@Override
	protected void actionOnTouch(MotionEvent e) {

		// System.out.println("X " + x);
		// System.out.println("Y " + y);

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xPress = e.getX();
			yPress = e.getY();
			break;

		case MotionEvent.ACTION_UP:
			xRelease = e.getX();
			yRelease = e.getY();

			float xDelta = xPress - xRelease;
			float yDelta = yPress - yRelease;

			if (Math.abs(xDelta) > SWIPE_MIN_DISTANCE) {
				// Left to Right
				if (xPress < xRelease) {
					DirectionPacket p = new DirectionPacket();
					p.directionX = 1;
					p.directionY = 0;
					new SendPacket().execute(p);
					// direction.set(1, 0);
				}

				// Right to Left
				if (xPress > xRelease) {
					DirectionPacket p = new DirectionPacket();
					p.directionX = -1;
					p.directionY = 0;
					new SendPacket().execute(p);
					// direction.set(-1, 0);
				}

				if (Math.abs(yDelta) > SWIPE_MIN_DISTANCE) {
					// Top to Bottom
					if (yPress < yRelease) {
						DirectionPacket p = new DirectionPacket();
						p.directionX = 0;
						p.directionY = 1;
						new SendPacket().execute(p);
						// direction.set(0, 1);
					}

					// Bottom to Top
					if (yPress > yRelease) {
						DirectionPacket p = new DirectionPacket();
						p.directionX = 0;
						p.directionY = -1;
						new SendPacket().execute(p);
						// direction.set(0, -1);
					}
				}
				break;
			}
		}
	}

	// This is run just before the game "scenario" is printed on the screen
	@Override
	protected void updateGame(float secondsElapsed) {
		for (Player player : MainActivity.players) {
			player.update();
		}
		// // player/pellet collision
		// for(RectF rect : pellets)
		// {
		// if(myBox.intersect(rect))
		// {
		// //System.out.println("INTERSECTION");
		// addPellet(rect);
		// playerSize *= 1.1;
		// speed *= 0.9;
		// if(speed <= 1)
		// speed = 1;
		//
		// updateScore(1);
		// }
		// }
		//
		// // border check
		// if(myBox.left < 0 || myBox.right > mCanvasWidth ||
		// myBox.top < 0 || myBox.bottom > mCanvasHeight)
		// {
		// this.setupBeginning(false);
		// }
	}

	// add pellet
	// essentially relocate the "eaten" pellet
	private RectF addPellet(RectF rect) {
		// X,Y are in the center of squares
		// add/subtract the size to make sure that pellets are always in the
		// boundaries of the map
		int randX = randInt(pelletSize, mCanvasWidth - pelletSize);
		int randY = randInt(pelletSize, mCanvasHeight - pelletSize);

		rect.set(new RectF(randX - pelletSize, randY - pelletSize, randX + pelletSize, randY
				+ pelletSize));

		return rect;
	}

	// gets a random integer for the pellet location
	// took from:
	// "http://stackoverflow.com/questions/363681/generating-random-numbers-in-a-range-with-java"
	private int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}