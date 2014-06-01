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
	
	public Player(float x, float y, float size){
		rect = new RectF(x, y, x + size, y + size);
	}
	
	public void update(){
		rect.set(x, y, x + size, y + size);
	}
}
