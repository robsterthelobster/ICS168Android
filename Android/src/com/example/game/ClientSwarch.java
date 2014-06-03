package com.example.game;

import java.util.ArrayList;

import com.example.game.network.*;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

public class ClientSwarch extends GameThread {

	private float xPress, yPress, xRelease, yRelease;

	private static final int SWIPE_MIN_DISTANCE = 120;

	public static final int pelletSize = 1920 / 50;
	public static ArrayList<RectF> pellets = new ArrayList<RectF>();

	// This is run before anything else, so we can prepare things here
	public ClientSwarch(GameView gameView) {
		// House keeping
		super(gameView);

		// just declaring stuff here, values are basically ignored
		// look at setUpBeginning for new games values
		paint = new Paint();
	}

	// This is run before a new game (also after an old game)
	@Override
	public void setupBeginning(boolean firstTimeSetUp) {

		//setScore(0);
	}

	@Override
	protected void doDraw(Canvas canvas) {
		if (canvas == null)
			return;

		// House keeping
		// this only draws the background, we can change it when deemed
		// necessary
		super.doDraw(canvas);
		
		// draw pellets as white
		paint.setColor(Color.GRAY);
		for (RectF pellet : pellets) {
			canvas.drawRect(pellet, paint);
		}

		// draw player as blue
		for (int i = 0; i < MainActivity.players.size(); i++) {
			paint.setColor(MainActivity.players.get(i).color);
			canvas.drawRect(MainActivity.players.get(i).rect, paint);
		}
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(48f);
		
		for(int i = 0; i < MainActivity.players.size(); i++){
			Player player = MainActivity.players.get(i);
			if(player != null){
				String score = "";
				score += player.name + ": " + player.score +"\n";
				canvas.drawText(score, 100, 100 + 48f * i, paint);
			}
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
					break;
				}

				// Right to Left
				else if (xPress > xRelease) {
					DirectionPacket p = new DirectionPacket();
					p.directionX = -1;
					p.directionY = 0;
					new SendPacket().execute(p);
					// direction.set(-1, 0);
					break;
				}
			}

			if (Math.abs(yDelta) > SWIPE_MIN_DISTANCE) {
				// Top to Bottom
				if (yPress < yRelease) {
					DirectionPacket p = new DirectionPacket();
					p.directionX = 0;
					p.directionY = 1;
					new SendPacket().execute(p);
					// direction.set(0, 1);
					break;
				}

				// Bottom to Top
				if (yPress > yRelease) {
					DirectionPacket p = new DirectionPacket();
					p.directionX = 0;
					p.directionY = -1;
					new SendPacket().execute(p);
					// direction.set(0, -1);
					break;
				}
			}
		}
	}

	// This is run just before the game "scenario" is printed on the screen
	@Override
	protected void updateGame(float secondsElapsed) {
		for (Player player : MainActivity.players) {
			player.update(secondsElapsed);
		}
	}
}