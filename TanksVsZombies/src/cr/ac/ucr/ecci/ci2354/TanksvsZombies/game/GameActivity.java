package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TickerText;
import org.andengine.entity.text.TickerText.TickerTextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.Intent;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui.GameOverActivity;

public class GameActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {

//	private static final String TAG = "GameActivity";

	private static final int CAMERA_WIDTH = 360;
	private static final int CAMERA_HEIGHT = 240;
	
	private static final int TANK_WIDTH = 50;
	private static final int TANK_HEIGHT = 50;
	
	private static final int BULLET_VELOCITY = -30;
	private static final int ZOMBIE_VELOCITY = 1;

	private static final float DELAY_BULLET = 0.5f;
	private static final float DELAY_ZOMBIE = 2f;

	private static final String REMAINING_LIFES_STRING = "Vidas Restantes: ";
	private static final String SCORE_STRING = "Puntaje: ";
	private static final float FONT_SIZE = 12;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mTankTexture;
	private TiledTextureRegion mBulletTexture;
	private TiledTextureRegion mZombieTexture;

	private AnimatedSprite mTank;

	private Scene mScene;
	private PhysicsWorld mPhysicsWorld;
	private float mGravityX;
	private Font mFont;
	// private float mGravityY;

	private Text text;

	private Game game = new Game(this);

	private boolean allowBulletCreation = true;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 100, 100, TextureOptions.BILINEAR);
		this.mTankTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "tankTransparent.png", 0, 0, 1, 1);
		this.mBulletTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "bullet.png", 51, 0, 1, 1);
		this.mZombieTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "zombieP.png", 72, 0, 1, 1);
		this.mBitmapTextureAtlas.load();
		
		// Para la fuente
		ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		FontFactory.setAssetBasePath("font/");
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), fontTexture, this.getAssets(), "Adventure.ttf", FONT_SIZE, true, Color.BLACK.getARGBPackedInt());
		this.mFont.load();
		
	
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_DEATH_STAR_I), false); // death
																											// star
																											// gravity!!!!

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(Color.WHITE));
		this.mScene.setOnSceneTouchListener(this);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		this.mScene.setOnAreaTouchListener(this);

		mPhysicsWorld.setContactListener(new GameContactListener(this, game));

		// Creador de Zombies
		getEngine().registerUpdateHandler(new TimerHandler(DELAY_ZOMBIE, true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				addZombie((float) (Math.random() * CAMERA_HEIGHT), 0f);

			}
		}));

		createRectangle();

		addTank(CAMERA_WIDTH / 2 - TANK_WIDTH / 2, CAMERA_HEIGHT - TANK_HEIGHT);

		text = new TickerText(10, 10, this.mFont, REMAINING_LIFES_STRING + game.getRemainingLife() + "\n" + SCORE_STRING + game.getScore(), new TickerTextOptions(HorizontalAlign.LEFT, 5), this.getVertexBufferObjectManager());
//		text.registerEntityModifier(
//			new SequenceEntityModifier(
//				new ParallelEntityModifier(
//					new AlphaModifier(10, 0.0f, 1.0f),
//					new ScaleModifier(10, 0.5f, 1.0f)
//				),
//				new RotationModifier(5, 0, 360)
//			)
//		);
//		text.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		this.mScene.attachChild(text);
		
		return this.mScene;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		boolean result = false;
		if (this.mPhysicsWorld != null) {
			if (pSceneTouchEvent.isActionDown() && allowBulletCreation) {
				allowBulletCreation = false;
				result = true;

				getEngine().runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						addBullet();
					}
				});

			}
		}

		return result;
	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		this.mGravityX = pAccelerationData.getX();
		// this.mGravityY = pAccelerationData.getY();
		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, 0);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		this.disableAccelerationSensor();
	}

	private void createRectangle() {
		int rectanglePX[] = { 0, 0, 0, CAMERA_WIDTH - 2 };
		int rectanglePY[] = { CAMERA_HEIGHT - 2, 0, 0, 0 };
		int retangleWidth[] = { CAMERA_WIDTH, CAMERA_WIDTH, 2, 2 };
		int rectangleHeigth[] = { 2, 2, CAMERA_HEIGHT, CAMERA_HEIGHT };
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0f, 0.5f);
		for (int i = 0; i < 4; ++i) {
			final Rectangle r = new Rectangle(rectanglePX[i], rectanglePY[i], retangleWidth[i], rectangleHeigth[i], vertexBufferObjectManager);
			PhysicsFactory.createBoxBody(this.mPhysicsWorld, r, BodyType.StaticBody, wallFixtureDef);
			this.mScene.attachChild(r);
		}
	}

	private void addTank(final float pX, final float pY) {
		mTank = createAnimatedSprite(100f, pX, pY, mTankTexture, Game.TANK_TYPE, BodyType.DynamicBody);
		((Body) mTank.getUserData()).setFixedRotation(true);
	}

	private void addZombie(final float pX, final float pY) {
		final AnimatedSprite zombie = createAnimatedSprite(0.1f, pX, pY, mZombieTexture, Game.NORMAL_ZOMBIE_TYPE, BodyType.KinematicBody);
		moveSprite(ZOMBIE_VELOCITY, (Body) zombie.getUserData());
	}

	private void addBullet() {

		float pX = mTank.getX() + TANK_WIDTH / 4;
		float pY = mTank.getY() - TANK_HEIGHT / 2;

		final AnimatedSprite bullet = createAnimatedSprite(0.1f, pX, pY, mBulletTexture, Game.BULLET_TYPE, BodyType.KinematicBody);

		moveSprite(BULLET_VELOCITY, (Body) bullet.getUserData());

		getEngine().registerUpdateHandler(new TimerHandler(DELAY_BULLET, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				getEngine().getTouchController().reset(); // Peligroso
				allowBulletCreation = true;
			}
		}));
	}

	private void moveSprite(int velocity, Body b) {
		Vector2 vector = Vector2Pool.obtain(0, velocity);
		b.setLinearVelocity(vector);
		Vector2Pool.recycle(vector);
	}

	private AnimatedSprite createAnimatedSprite(float density, float pX, float pY, TiledTextureRegion t, int type, BodyType bodyType) {

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(density, 0.1f, 0.5f);

		AnimatedSprite sprite = new AnimatedSprite(pX, pY, t, this.getVertexBufferObjectManager());

//		final Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, sprite, BodyType.DynamicBody, objectFixtureDef);
		final Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, sprite, bodyType, objectFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(sprite, body, true, true));

		SpriteHolder holder = new SpriteHolder();
		holder.sprite = sprite;
		holder.type = type;
		holder.body = body;

		body.setUserData(holder); // El userData del body es el sprite y el tipo
		sprite.setUserData(body); // El userData del sprite es el body

		this.mScene.registerTouchArea(sprite);
		this.mScene.attachChild(sprite);

		return sprite;
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
	}

	public void updateText() {
		text.setText(REMAINING_LIFES_STRING + game.getRemainingLife() + "\n" + SCORE_STRING + game.getScore());
	}

	public void gameOver() {
		Intent intent = new Intent(this, GameOverActivity.class);
		intent.putExtra("puntuacion", game.getScore());
		startActivity(intent);
		finish();
	}

	public static class SpriteHolder {
		public AnimatedSprite sprite;
		public Body body;
		public int type; // 1 == bullet
	}

}