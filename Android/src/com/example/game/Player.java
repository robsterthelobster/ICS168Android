package com.example.game;

import android.graphics.RectF;

public class Player {
	
	public int id;
	public float x;
	public float y;
	public int directionX;
	public int directionY;
	public float size;
	public float speed;
	public RectF rect;
	public String name;
	public int score;
	public int color;
		
	public Player(float x, float y, float size){
		rect = new RectF(x, y, x + size, y + size);
	}
	
	public void update(float secondsElapsed){
		x += directionX * speed * secondsElapsed;
		y += directionY * speed * secondsElapsed;
		rect.set(x, y, x + size, y + size);
	}
}
