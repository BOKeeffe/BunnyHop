package wit.cgd.bunnyhop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import wit.cgd.bunnyhop.util.Constants;



public class WorldRenderer implements Disposable {

	private static final String TAG = WorldRenderer.class.getName();

	Constants constants;
	
	private OrthographicCamera camera;
	private OrthographicCamera cameraGUI;
	private SpriteBatch batch;
	private WorldController worldController;
	//public float timeLeft;

	public WorldRenderer (WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
	}

	public void render () {
		renderWorld(batch);
		renderGui(batch);
	}

	private void renderWorld (SpriteBatch batch) {
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();
	}

	private void renderGui (SpriteBatch batch) {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();

		// draw collected gold coins icon + text (anchored to top left edge)
		renderGuiScore(batch);
		// draw collected feather icon (anchored to top left edge)
		renderGuiFeatherPowerup(batch);
		// draw extra lives icon + text (anchored to top right edge)
		renderGuiExtraLive(batch);
		// draw FPS text (anchored to bottom right edge)
		renderGuiFpsCounter(batch);
		// draw game over text
		renderGuiGameOverMessage(batch);
		//draw game win text
		renderGuiGameWinMessage(batch);
		//draw time left Gui
		renderGuiTimeLeft(batch);
		batch.end();
	}


	private void renderGuiScore (SpriteBatch batch) {
		float x = -15;
		float y = -15;
		batch.draw(Assets.instance.goldCoin.goldCoin, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch, "" + worldController.score, x + 75, y + 37);
	}
	
	
	private void renderGuiTimeLeft (SpriteBatch batch) {
		float x = -15;
		float y = 30;
		worldController.timeLeft = Constants.TIMER_COUNTDOWN;
		if(worldController.timeLeft > 0){
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultBig.draw(batch, "" + (int)worldController.timeLeft, x + 750, y + 40);
		}
		//batch.setColor(1, 1, 1, 1);
		//Assets.instance.fonts.defaultBig.draw(batch, "" + (int)worldController.timeLeft, x + 750, y + 40);
	}
	

	private void renderGuiFeatherPowerup (SpriteBatch batch) {
		float x = -15;
		float y = 30;
		float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;
		if (timeLeftFeatherPowerup > 0) {
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds. The fade interval is set
			// to 5 changes per second.
			if (timeLeftFeatherPowerup < 4) {
				if (((int)(timeLeftFeatherPowerup * 5) % 2) != 0) {
					batch.setColor(1, 1, 1, 0.5f);
				}
			}
			batch.draw(Assets.instance.feather.feather, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultSmall.draw(batch, "" + (int)timeLeftFeatherPowerup, x + 60, y + 57);
		}
	}

	public void renderGuiExtraLive (SpriteBatch batch) {
 		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
		float y = -15;
		for (int i = 0; i < Constants.LIVES_START; i++) {
			
			//Render new lives to screen
			if(worldController.extraLiveScore == 2500){
				Constants.LIVES_START++;
				worldController.lives++;
				worldController.extraLiveScore = 0;
				if (worldController.lives <= i) batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
				batch.draw(Assets.instance.bunny.head, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
				batch.setColor(1, 1, 1, 1);
				System.out.println("extra live");
			}
			else
			{
				if (worldController.lives <= i) batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
				batch.draw(Assets.instance.bunny.head, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
				batch.setColor(1, 1, 1, 1);
			
			}
		}
	}

	private void renderGuiFpsCounter (SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if (fps >= 45) {
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else if (fps >= 30) {
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		} else {
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}

		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}

	private void renderGuiGameOverMessage (SpriteBatch batch) {
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		if (worldController.isGameOver()) {
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1);
			fontGameOver.drawMultiLine(batch, "GAME OVER", x, y, 1, BitmapFont.HAlignment.CENTER);
			fontGameOver.setColor(1, 1, 1, 1);
		}
	}
	
	public void renderNewGameMessage (SpriteBatch batch) {
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		if (worldController.isGameWon()) {
			BitmapFont fontNewGame = Assets.instance.fonts.defaultBig;
			fontNewGame.setColor(1, 0.75f, 0.25f, 1);
			fontNewGame.drawMultiLine(batch, "New Game Started", x, y, 1, BitmapFont.HAlignment.CENTER);
			fontNewGame.setColor(1, 1, 1, 1);
		}
	}
	
	/**
	 * Game win Gui,: displayed when the bunny reaches the Goal
	 * @param batch
	 */
	public void renderGuiGameWinMessage (SpriteBatch batch) {
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;
		if (worldController.isGameWon()) {
			BitmapFont fontGameWin = Assets.instance.fonts.defaultBig;
			fontGameWin.setColor(1, 0.75f, 0.25f, 1);
			fontGameWin.drawMultiLine(batch, "Congratulations You Win", x, y, 1, BitmapFont.HAlignment.CENTER);
			fontGameWin.setColor(1, 1, 1, 1);
		}
	}

	public void resize (int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
		camera.update();
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / height) * width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}


}
