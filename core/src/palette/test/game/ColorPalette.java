package palette.test.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ColorPalette extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background, effect, colMap;
	private TextureAtlas wizardAtlas;
	private Animation<TextureRegion> wizardAnimation;
	private ShaderProgram shader;
	private float elaTime, elaTime2;
	private Array<PaletteHitbox> paletteHitboxes;

	private OrthographicCamera cam;
	private Viewport viewport;
	private float paletteIndex;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("textures/tiling_many.png");
		effect = new Texture("textures/plasma.png");
		colMap = new Texture("gameplay/color_lut.png");
		wizardAtlas = new TextureAtlas("gameplay/mage_idle_coords.atlas");
		wizardAnimation = new Animation<TextureRegion>(0.2f, wizardAtlas.findRegions("mage"), Animation.PlayMode.NORMAL);
		elaTime=0;
		elaTime2=0;

		cam = new OrthographicCamera(1600,900);
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		viewport = new FitViewport(1600, 900, cam);
		cam.update();

		//Create a hitbox for each color palette
		paletteIndex=0.083f;
		paletteHitboxes = new Array<PaletteHitbox>();
		for(int i=0; i<6; i++){
			paletteHitboxes.add(new PaletteHitbox(1250+50*i,50,50,550, (i+0.5f)/6));
		}

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		shader = new ShaderProgram(Gdx.files.internal("shaders/passthrough.vsh"), Gdx.files.internal("shaders/passthrough.fsh"));

		batch.setShader(shader);

		colMap.bind(1);
		shader.begin();
		shader.setUniformi("u_color_table", 1);
		shader.end();
	}

	@Override
	public void render () {

		//If the mouse hover over a certain palette, paletteIndex will be set to it's corresponding index
		checkMouseHitboxes();

		cam.update();
		batch.setProjectionMatrix(cam.combined);

		Gdx.gl.glClearColor(0.8f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		//Default blend function
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClearDepthf(1f);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);


		batch.begin();
		batch.setShader(null);
		batch.draw(background, 0, 0, 900, 900);
		batch.draw(background, 866f, 0, 900, 900);

		batch.flush();
		//using depth so the effect pattern is only drawn in the square of the sprite
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);
		//depth won't ignore transparent pixels on the sprite --> use blend functions for that
		//only writing alpha to buffer
		Gdx.gl.glColorMask(false, false, false, true);
		//Everything from the source will be used --> fully transparent pixels will be written as transparent and won't combine with destination
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		batch.draw(wizardAnimation.getKeyFrame(elaTime), 530-150*elaTime2, 50, 500, 500);

		batch.flush();
		//using depth so the effect pattern is only drawn in the square of the sprite
		Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
		//writing colors again
		Gdx.gl.glColorMask(true, true, true, true);
		//effect will be used where destination has non-transparent pixels, destination colors (backgroudn drawn before) where it doesn't
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		batch.draw(effect,0,0,1600,900);

		batch.flush();
		//want to draw everything past this point, not comparing depth anymore
		Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.draw(wizardAnimation.getKeyFrame(elaTime), 530, 50, 500, 500);
		batch.draw(colMap, 1250,50,300,550);
		//working of the color table explained in the shader
		batch.setShader(shader);
		shader.setUniformf("u_palette_index", paletteIndex);
		batch.draw(wizardAnimation.getKeyFrame(elaTime), 820, 50, 500, 500);
		batch.end();

		elaTime += Gdx.graphics.getDeltaTime();
		elaTime2 += Gdx.graphics.getDeltaTime();
		elaTime %=2;
		elaTime2 %=4;
	}

	public void checkMouseHitboxes(){
		Vector3 mousePosition = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		for(PaletteHitbox palette: paletteHitboxes){
			if(palette.overlaps(mousePosition.x, mousePosition.y)){
				paletteIndex=palette.position;
			}
		}
	}

	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		effect.dispose();
		colMap.dispose();
		wizardAtlas.dispose();
	}
}
