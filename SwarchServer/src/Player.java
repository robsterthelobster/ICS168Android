
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
			x = startX;
			y = startY;
		}
	}
}
