package com.example.game;
//Other parts of the android libraries that we use

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Swarch extends GameThread{
	
	// every game entity is a square
	private Rect myBox;
	private int playerSize = 50;
	private int playerX, playerY;
	private int dx, dy;
	private float speed;
	
	private ArrayList<Rect> pellets;
	private int pelletSize = 20;
	
	private Paint paint;
	
	private boolean motionEnabled = false;
	
	private float xPress, yPress, xRelease, yRelease;
	
	private static final int SWIPE_MIN_DISTANCE = 120;

	//This is run before anything else, so we can prepare things here
	public Swarch(GameView gameView) {
		//House keeping
		super(gameView);
		
		// just declaring stuff here, values are basically ignored
		// look at setUpBeginning for new games values
		paint = new Paint();
		pellets = new ArrayList<Rect>();
		myBox = new Rect();
	}

	//This is run before a new game (also after an old game)
	@Override
	public void setupBeginning() {
		
		// player coordinates set here
		playerX = mCanvasWidth/2;
		playerY = mCanvasHeight/2;
		myBox.set(playerX - playerSize, playerY - playerSize, playerX + playerSize, playerY + playerSize);
		
		speed = 8;

		// original 4 random 4 pellets
		// pellet is 5 by 5 square
		for(int i = 0; i < 4; i++)
		{
			int randX = randInt(0, mCanvasWidth);
			int randY = randInt(0, mCanvasHeight);
			
			pellets.add(new Rect(randX - pelletSize, randY - pelletSize, randX + pelletSize, randY + pelletSize));
		}
	}

	@Override
	protected void doDraw(Canvas canvas) {
		//If there isn't a canvas to do nothing
		//It is ok not understanding what is happening here
		if(canvas == null) return;
		
		//House keeping
		super.doDraw(canvas);

		// draw player as blue
		paint.setColor(Color.BLUE);
		canvas.drawRect(myBox, new Paint());
		
		// draw pellets as white
		paint.setColor(Color.WHITE);
		for(Rect rect: pellets)
		{
			canvas.drawRect(rect, new Paint());
		}
	}

	//This is run whenever the phone is touched by the user
	@Override
	protected void actionOnTouch(MotionEvent e) {

		//System.out.println("X " + x);
		//System.out.println("Y " + y);
		
		switch(e.getAction())
    	{
    		case MotionEvent.ACTION_DOWN:
    		{
    			xPress = e.getX();
    			yPress = e.getY();    			
    			
    			break;
    		}
    		
    		case MotionEvent.ACTION_UP:
    		{
    			xRelease = e.getX();
    			yRelease = e.getY();
    			
    			float xDelta = xPress - xRelease;
    			float yDelta = yPress - yRelease;
    			
    			if (Math.abs(xDelta) > SWIPE_MIN_DISTANCE)
    			{
    				// Left to Right
    				if (xPress < xRelease)
    				{
    					dx = 1;
    					dy = 0;
    				}

    				// Right to Left
    				if (xPress > xRelease)
    				{
    					dx = -1;
    					dy = 0;
    				}
    			}
    			
    			if (Math.abs(yDelta) > SWIPE_MIN_DISTANCE)
    			{
	    			// Top to Bottom
	    			if (yPress < yRelease)
	    			{
	    				dx = 0;
	    				dy = 1;
	    			}
	    			
	    			// Bottom to Top
	    			if (yPress > yRelease)
	    			{
	    				dx = 0;
	    				dy = -1;	    		
	    			}	    	
    			}
    			break;
    		}
    	}
	}
	
	//This is run whenever the phone moves around its axises 
	@Override
	protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
		
		if(motionEnabled)
		{
			// if the x tilt is greater than the y tilt
			// prioritize the x tilt for player controls
			// else, prioritize y tilt for player controls
			if(Math.abs(xDirection) > Math.abs(yDirection))
			{
				if(xDirection < 0)
					dy = -1;
				else if(xDirection > 0)
					dy = 1;
				dx = 0;
			}
			else
			{
				if(yDirection < 0)
					dx = 1;
				else if(yDirection > 0)
					dx = -1;
				dy = 0;
			}
		}
	}
	 

	//This is run just before the game "scenario" is printed on the screen
	@Override
	protected void updateGame(float secondsElapsed) {
		playerX += dx * speed;
		playerY += dy * speed;
				
		// border check
		if(myBox.left < 0 || myBox.right > mCanvasWidth ||
				myBox.top < 0 || myBox.bottom > mCanvasHeight)
		{
			playerX = mCanvasWidth/2;
			playerY = mCanvasHeight/2;
			setScore(0);
			speed = 8;
			playerSize = 50;
		}
		
		myBox.set(playerX - playerSize, playerY - playerSize, playerX + playerSize, playerY + playerSize);
		
		for(Rect rect : pellets)
		{
			if(myBox.intersect(rect))
			{
				System.out.println("INTERSECTION");
				int randX = randInt(0, mCanvasWidth);
				int randY = randInt(0, mCanvasHeight);
				rect.set(new Rect(randX - pelletSize, randY - pelletSize, randX + pelletSize, randY + pelletSize));
				playerSize += 10;
				speed *= 0.9;
				if(speed <= 1)
					speed = 1;
				
				updateScore(1);
			}
		}
	}
	
	// gets a random integer for the pellet location
	// took from:
	// "http://stackoverflow.com/questions/363681/generating-random-numbers-in-a-range-with-java"
	private int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// 
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.
