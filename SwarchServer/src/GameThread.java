public abstract class GameThread extends Thread {
	//Different mMode states
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;

	//Control variable for the mode of the game (e.g. STATE_WIN)
	protected int mMode = 1;

	//Control of the actual running inside run()
	private boolean mRun = true;
	
	//We might want to extend this call - therefore protected
	protected int mCanvasWidth = 1;
	protected int mCanvasHeight = 1;

	//Last time we updated the game physics
	protected long mLastTime = 0;
 
	protected long score = 0;
	
	private long now;
	private float elapsed;
	
	public GameThread() {		
	}
	
	/*
	 * Called when app is destroyed, so not really that important here
	 * But if (later) the game involves more thread, we might need to stop a thread, and then we would need this
	 * Dare I say memory leak...
	 */
	public void cleanup() {		
	}
	
	//Pre-begin a game
	abstract public void setupBeginning(boolean firstTimeSetUp);
	
	//Starting up the game
	public void doStart() {
			
			setupBeginning(true);
			
			mLastTime = System.currentTimeMillis() + 100;

			
	}
	
	//The thread start
	@Override
	public void run() {
		while (mRun) {
						updatePhysics();
					doDraw();
			} 
	}
	
	/*
	 * Surfaces and drawing
	 */
	public void setSurfaceSize(int width, int height) {
			mCanvasWidth = width;
			mCanvasHeight = height;
	}


	protected void doDraw() {
		
	}
	
	private void updatePhysics() {
		now = System.currentTimeMillis();
		elapsed = (now - mLastTime) / 1000.0f;

		updateGame(elapsed);

		mLastTime = now;
	}
	
	abstract protected void updateGame(float secondsElapsed);
	
}

