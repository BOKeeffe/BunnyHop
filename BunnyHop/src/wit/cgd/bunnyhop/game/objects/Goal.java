package wit.cgd.bunnyhop.game.objects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import wit.cgd.bunnyhop.game.Assets;

public class Goal extends AbstractGameObject {

	private TextureRegion regGoal;
	public boolean collected;
	

	public Goal () {
		init();
	}

	private void init () {
		dimension.set(3.0f, 3.0f);
		regGoal = Assets.instance.goal.goal;
		origin.set(dimension.x / 2, dimension.y / 2);

		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}

	@Override
	public void render (SpriteBatch batch) {
		if(collected) return;
		TextureRegion reg = null;
		reg = regGoal;
		batch.draw(reg.getTexture(), position.x - origin.x, position.y - origin.y, origin.x, origin.y, dimension.x, dimension.y,
			scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false,
			false); 
	}

}

