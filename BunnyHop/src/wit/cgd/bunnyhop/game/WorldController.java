package wit.cgd.bunnyhop.game;

import wit.cgd.bunnyhop.game.objects.BunnyHead;
import wit.cgd.bunnyhop.game.objects.BunnyHead.JUMP_STATE;
import wit.cgd.bunnyhop.game.objects.Feather;
import wit.cgd.bunnyhop.game.objects.GoldCoin;
import wit.cgd.bunnyhop.game.objects.Rock;
import wit.cgd.bunnyhop.util.CameraHelper;
import wit.cgd.bunnyhop.util.Constants;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class WorldController extends InputAdapter {

	private static final String TAG = WorldController.class.getName();


	//private Game game;
	public Level level;
	public int lives;
	public int score;
	public float timeLeft;
	public int extraLiveScore;
	
	
	public Feather              feather;
	public WorldRenderer 		worldRenderer;
	public CameraHelper cameraHelper;
	private boolean goalReached ;

	// Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();

	private float timeLeftGameOverDelay;
	private float timeLeftGameWinDelay;

	public WorldController () {
		
		//this.game = game;
		init();
	}

	private void init () {
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		timeLeftGameOverDelay = 0;
		timeLeftGameWinDelay = 0;
		initLevel();
	}

	private void initLevel () {
		score = 0;
		extraLiveScore = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.bunnyHead);
		//cameraHelper.setTarget(level.goal);
	}

	public void update (float deltaTime) {
		handleDebugInput(deltaTime);
	
		if(isGameWon()){
			timeLeftGameWinDelay -= deltaTime;
		}
		else{
			handleDebugInput(deltaTime);
		}
		if (isGameOver()) {
			timeLeftGameOverDelay -= deltaTime;
		} else {
			handleInputGame(deltaTime);
		}
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
		
		if (!isGameOver() && isPlayerInWater()) {
			lives--;
			if (isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
		
			else
				initLevel();
		}
		
	}

	public boolean isGameOver () {
		return lives < 0;
	}

	public boolean isPlayerInWater () {
		return level.bunnyHead.position.y < -4;
	}

	private void testCollisions () {
		r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);

		// Test collision: Bunny Head <-> Rocks
		for (Rock rock : level.rocks) {
			r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyHeadWithRock(rock);
			// IMPORTANT: must do all collisions for valid edge testing on rocks.
		}

		// Test collision: Bunny Head <-> Gold Coins
		for (GoldCoin goldcoin : level.goldcoins) {
			if (goldcoin.collected) continue;
			r2.set(goldcoin.position.x, goldcoin.position.y, goldcoin.bounds.width, goldcoin.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithGoldCoin(goldcoin);
			break;
		}

		// Test collision: Bunny Head <-> Feathers
		for (Feather feather : level.feathers) {
			if (feather.collected) continue;
			r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithFeather(feather);
			break;
		}
		
		if (!goalReached) {
			r2.set(level.goal.bounds);
			r2.x += level.goal.position.x;
			r2.y += level.goal.position.y;
			if (r1.overlaps(r2)) onCollisionBunnyWithGoal();
		}
	}

	private void onCollisionBunnyHeadWithRock (Rock rock) {
		BunnyHead bunnyHead = level.bunnyHead;
		float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
		if (heightDifference > 0.25f) {
			boolean hitLeftEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
			if (hitLeftEdge) {
				bunnyHead.position.x = rock.position.x + rock.bounds.width;
			} else {
				bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
			}
			return;
		}

		switch (bunnyHead.jumpState) {
		case GROUNDED:
			break;
		case FALLING:
		case JUMP_FALLING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
			bunnyHead.jumpState = JUMP_STATE.GROUNDED;
			break;
		case JUMP_RISING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
			break;
		}
	}
	
	public boolean  isGameWon(){
		if(goalReached == true)
			System.out.println("***********   Player wins   *****************");
		return goalReached;
	}

	private void onCollisionBunnyWithGoldCoin (GoldCoin goldcoin) {
		goldcoin.collected = true;
		score += goldcoin.getScore();
		extraLiveScore += goldcoin.getScore();
		Gdx.app.log(TAG, "Gold coin collected");
		if(score >= 3500){
			//feather.collected = true;
			//score += feather.getScore();
			level.bunnyHead.setFeatherPowerup(true);
			score = 0;
		}
	}
	
	private void onCollisionBunnyWithGoal () {
		goalReached = true;
		timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_WIN;
		Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position);
		centerPosBunnyHead.x += level.bunnyHead.bounds.width;
		isGameWon();
		
	}


	private void onCollisionBunnyWithFeather (Feather feather) {
		feather.collected = true;
		//score += feather.getScore();
		level.bunnyHead.setFeatherPowerup(true);
		if(score == 3500){
			Gdx.app.log(TAG, "2500 coins free feather earned");
		}
		else
		Gdx.app.log(TAG, "Feather collected");
	}

	private void handleDebugInput (float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;

		if (!cameraHelper.hasTarget(level.bunnyHead)) {
			// Camera Controls (move)
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
		}

		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
	}

	private void handleInputGame (float deltaTime) {
		if (cameraHelper.hasTarget(level.bunnyHead)) {
			// Player Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			} else {
				// Execute auto-forward movement on non-desktop platform
				if (Gdx.app.getType() != ApplicationType.Desktop) {
					level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
				}
			}

			// Bunny Jump
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
				level.bunnyHead.setJumping(true);
			else
				level.bunnyHead.setJumping(false);
		}
	}

	private void moveCamera (float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	@Override
	public boolean keyUp (int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		// Toggle camera follow
		else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
			Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
		}
		return false;
	}

	public int getExtraLiveScore() {
		return extraLiveScore;
	}

	public void setExtraLiveScore(int extraLiveScore) {
		this.extraLiveScore = extraLiveScore;
	}
	
	


}
