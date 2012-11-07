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
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.game.VerticalParallaxBackground.VerticalParallaxEntity;

public class GameActivity extends SimpleBaseGameActivity implements
		IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {

	private static final String TAG = "GameActivity";

	private static final int CAMERA_WIDTH = 360;
	private static final int CAMERA_HEIGHT = 240;
	private static final int TANK_WIDTH = 50;
	private static final int TANK_HEIGHT = 50;
	private static final int BULLET_VELOCITY = -30;
	private static final int ZOMBIE_VELOCITY = 1;
	private static final int ANY_TYPE = 0;
	private static final int TANK_TYPE = 1;
	private static final int BULLET_TYPE = 2;
	private static final int NORMAL_ZOMBIE_TYPE = 3;
	private static final float DELAY_BULLET = 0.5f;
	private static final float DELAY_ZOMBIE = 2f;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mTankTexture;
	private TiledTextureRegion mBulletTexture;
	private TiledTextureRegion mZombieTexture;
	private ITextureRegion mBackLayer; //la parte de atras
	private ITextureRegion mFrontLayer; // los arbolitos
	private BitmapTextureAtlas mParallaxTexture;

	private AnimatedSprite mTank;

	private Scene mScene;
	private PhysicsWorld mPhysicsWorld;
	private float mGravityX;
	// private float mGravityY;

	private boolean allowBulletCreation = true;

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 100, 100, TextureOptions.BILINEAR);
		this.mTankTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"tankTransparent.png", 0, 0, 1, 1);
		this.mBulletTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"bullet.png", 51, 0, 1, 1);
		this.mZombieTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"zombieP.png", 72, 0, 1, 1);
		this.mBitmapTextureAtlas.load();
		
		this.mParallaxTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024);
		this.mBackLayer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mParallaxTexture, this, "background.png", 0, 0);
		//this.mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_back.png", 0, 188);
		//this.mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_mid.png", 0, 669);
		this.mParallaxTexture.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_DEATH_STAR_I), false); // death star
																// gravity!!!!
		VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		//AutoParallaxBackground (float pRed, float pGreen, float pBlue, float pParallaxChangePerSecond)
		AutoVerticalParallaxBackground autoParallaxBackground = new AutoVerticalParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachVerticalParallaxEntity(new VerticalParallaxEntity(-2.0f, new Sprite(0, CAMERA_HEIGHT - this.mBackLayer.getHeight(),this.mBackLayer,vertexBufferObjectManager)));
		this.mScene = new Scene();
		this.mScene.setBackground(autoParallaxBackground);
		this.mScene.setOnSceneTouchListener(this);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		this.mScene.setOnAreaTouchListener(this);

		mPhysicsWorld.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void beginContact(Contact contact) {

				Body bodyVector[] = {contact.getFixtureA().getBody(), contact.getFixtureB().getBody()};
				SpriteHolder spriteVector[] = {null, null};
				for (int i = 0; i < 2; ++i) {
					if ((SpriteHolder) bodyVector[i].getUserData() != null) {
						spriteVector[i] = (SpriteHolder) bodyVector[i].getUserData();
					}
				}
				
				verifyCases(spriteVector[0], spriteVector[1]);
			}

			// permite nulos en los SpriteHolder
			private void verifyCases(SpriteHolder a, SpriteHolder b) {

				Log.d(TAG, "LOL " + (a == null) + " " + (b == null));
				
				int typeA[] = { BULLET_TYPE, ANY_TYPE, BULLET_TYPE, NORMAL_ZOMBIE_TYPE };
				int typeB[] = { ANY_TYPE, BULLET_TYPE,NORMAL_ZOMBIE_TYPE, BULLET_TYPE };
				boolean killA[] = {true, false, true, true};
				boolean killB[] = {false, true, true, true};

				for (int i = 0; i < 4; ++i) {
					if (verifyTypes(a, b, typeA[i], typeB[i])){
						killSprites(a, b, killA[i], killB[i]);
					}
				}
				
			}

			// permite nulos en los SpriteHolder
			private boolean verifyTypes(SpriteHolder a, SpriteHolder b,
					int typeA, int typeB) {
				return (a != null && a.type == typeA || typeA == ANY_TYPE)
						&& (b != null && b.type == typeB || typeB == ANY_TYPE);
			}

			private void killSprites(final SpriteHolder a,
					final SpriteHolder b, final boolean killA,
					final boolean killB) {

				getEngine().runOnUpdateThread(new Runnable() {
					public void run() {
						if (killA) {
							kill(a);
						}
						if (killB) {
							kill(b);
						}
					}

					private void kill(SpriteHolder victim) {
						victim.body.setActive(false);
						victim.sprite.setVisible(false);
					}
				});
			}

		});

		// Creador de Zombies
		getEngine().registerUpdateHandler(
				new TimerHandler(DELAY_ZOMBIE, true, new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						addZombie((float) (Math.random() * CAMERA_HEIGHT), 0f);

					}
				}));

		createRectangle();

		addTank(CAMERA_WIDTH / 2 - TANK_WIDTH / 2, CAMERA_HEIGHT - TANK_HEIGHT);

		return this.mScene;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
			final ITouchArea pTouchArea, final float pTouchAreaLocalX,
			final float pTouchAreaLocalY) {
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
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
		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0,
				0f, 0.5f);
		for (int i = 0; i < 4; ++i) {
			final Rectangle r = new Rectangle(rectanglePX[i], rectanglePY[i],
					retangleWidth[i], rectangleHeigth[i],
					vertexBufferObjectManager);
			PhysicsFactory.createBoxBody(this.mPhysicsWorld, r,
					BodyType.StaticBody, wallFixtureDef);
			this.mScene.attachChild(r);
		}
	}

	private void addTank(final float pX, final float pY) {
		// Log.d(GameActivity.TAG, "Tank position: X " + pX + " Y " + pY);
		mTank = createAnimatedSprite(100f, pX, pY, mTankTexture, TANK_TYPE);
		((Body) mTank.getUserData()).setFixedRotation(true);
	}

	private void addZombie(final float pX, final float pY) {
		// Log.d(GameActivity.TAG, "Zombie position: X " + pX + " Y " + pY);
		final AnimatedSprite zombie = createAnimatedSprite(0.1f, pX, pY,
				mZombieTexture, NORMAL_ZOMBIE_TYPE);

		moveSprite(ZOMBIE_VELOCITY, (Body) zombie.getUserData());
	}

	private void addBullet() {

		float pX = mTank.getX() + TANK_WIDTH / 4;
		float pY = mTank.getY() - TANK_HEIGHT / 2;
		// Log.d(GameActivity.TAG, "Bullet position: X " + pX + " Y " + pY);

		final AnimatedSprite bullet = createAnimatedSprite(0.1f, pX, pY,
				mBulletTexture, BULLET_TYPE);

		moveSprite(BULLET_VELOCITY, (Body) bullet.getUserData());

		getEngine().registerUpdateHandler(
				new TimerHandler(DELAY_BULLET, new ITimerCallback() {
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

	private AnimatedSprite createAnimatedSprite(float density, float pX,
			float pY, TiledTextureRegion t, int type) {

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(
				density, 0.1f, 0.5f);

		AnimatedSprite sprite = new AnimatedSprite(pX, pY, t,
				this.getVertexBufferObjectManager());

		final Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
				sprite, BodyType.DynamicBody, objectFixtureDef);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				sprite, body, true, true));

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

	private static class SpriteHolder {
		public AnimatedSprite sprite;
		public Body body;
		public int type; // 1 == bullet
	}

}