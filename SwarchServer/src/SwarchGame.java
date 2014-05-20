//http://www.java-gaming.org/index.php?topic=24220.0

import game.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class SwarchGame
{
   private boolean running = false;
   private boolean paused = false;
   private int fps = 60;
   private int frameCount = 0;
   
   private static ArrayList<Player> players;
   private ArrayList<Rectangle> pellets;
   private ArrayList<Point> locations;
   
   private int pelletSize;
   private int width = 1920;
   private int height = 1080;
   
   
   public SwarchGame()
   {
	   players = new ArrayList<Player>();
	   pellets = new ArrayList<Rectangle>();
	   locations = new ArrayList<Point>();
	   // 100, 100
	   // 1820, 100
	   // 1820, 980
	   // 100, 920
	   locations.add(new Point(100, 100));
	   locations.add(new Point(1820, 100));
	   locations.add(new Point(1820, 980));
	   locations.add(new Point(100, 980));
	   running = true;
   }
   
   public void init(boolean first){
	   pelletSize = height/50;
	   if(first){
		   for(int i = 0; i < 4; i++)
			   pellets.add(addPellet(new Rectangle()));
	   }
	   
   }
   
   //Starts a new thread and runs the game loop in it.
   public void runGameLoop()
   {
      Thread loop = new Thread()
      {
         public void run()
         {
            gameLoop();
         }
      };
      loop.start();
   }
   
   //Only run this in another Thread!
   private void gameLoop()
   {
      //This value would probably be stored elsewhere.
      final double GAME_HERTZ = 30.0;
      //Calculate how many ns each frame should take for our target game hertz.
      final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
      //At the very most we will update the game this many times before a new render.
      //If you're worried about visual hitches more than perfect timing, set this to 1.
      final int MAX_UPDATES_BEFORE_RENDER = 5;
      //We will need the last update time.
      double lastUpdateTime = System.nanoTime();
      //Store the last time we rendered.
      double lastRenderTime = System.nanoTime();
      
      //If we are able to get as high as this FPS, don't render again.
      final double TARGET_FPS = 60;
      final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
      
      //Simple way of finding FPS.
      int lastSecondTime = (int) (lastUpdateTime / 1000000000);
      
      while (running)
      {
         double now = System.nanoTime();
         int updateCount = 0;
         
         if (!paused)
         {
             //Do as many game updates as we need to, potentially playing catchup.
            while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
            {
               updateGame();
               lastUpdateTime += TIME_BETWEEN_UPDATES;
               updateCount++;
            }
   
            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
            {
               lastUpdateTime = now - TIME_BETWEEN_UPDATES;
            }
         
            //Render. To do so, we need to calculate interpolation for a smooth render.
            float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
            drawGame(interpolation);
            lastRenderTime = now;
         
            //Update the frames we got.
            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime)
            {
               //System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
               fps = frameCount;
               frameCount = 0;
               lastSecondTime = thisSecond;
            }
         
            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
            {
               Thread.yield();
            
               //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
               //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
               //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
               try {Thread.sleep(1);} catch(Exception e) {} 
            
               now = System.nanoTime();
            }
         }
      }
   }
   
   
   private void updateGame()
   {
	   //System.out.println("FPS: " + fps);
	   for(Player player : players){
		   player.x += player.directionX * player.speed;
		   player.y += player.directionY * player.speed;
		   
		   if(	player.x < 0 || player.x + player.size > width ||
				player.y < 0 || player.y + player.size > height){
			   
			   player.x = locations.get(player.num).x;
			   player.y = locations.get(player.num).y;
			   
		   }
	   }
   }
   
   private void drawGame(float interpolation)
   {
	   frameCount++;
   }
   
   private Rectangle addPellet(Rectangle rect){
	   int randX = randInt(pelletSize, width);
	   int randY = randInt(pelletSize, height);
	   
	   rect.x = randX;
	   rect.y = randY;
	   rect.height = pelletSize;
	   rect.width = pelletSize;
	   
	   return rect;
   }
   
   private int randInt(int min, int max){
	   return new Random().nextInt((max - min) + 1) + min;
   }
}