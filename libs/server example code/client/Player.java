import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;


public class Player {

	float speed = 2f;
	Vector2f position = new Vector2f(256,256);
	Vector2f networkPosition = new Vector2f(0,0);
	
	public void update(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			position.y += speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			position.y -= speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			position.x -= speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			position.x += speed;
		}
	}
}
