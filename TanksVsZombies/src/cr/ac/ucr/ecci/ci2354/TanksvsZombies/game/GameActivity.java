package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
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
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.Intent;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.game.VerticalParallaxBackground.VerticalParallaxEntity;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui.GameOverActivity;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui.MainMenuActivity;

public class GameActivity extends SimpleBaseGameActivity implements
		IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {

	private static final String TAG = "GameActivity";

	private static final int TANK_WIDTH = 50;
	private static final int TANK_HEIGHT = 50;

	private static final int BULLET_VELOCITY = -30;
	private static final int ZOMBIE_VELOCITY = 1;

	private static final float DELAY_BULLET = 0.5f;
	private static final float DELAY_ZOMBIE = 2f;
	private static final int REGRESAR_INICIO = 1;
	private static final int TERMINAR_PARTIDA = 2;

	private static final String REMAINING_LIFES_STRING = "Vidas Restantes: ";
	private static final String SCORE_STRING = "Puntaje: ";
	private static final float FONT_SIZE_HUD = 12;
	private static final float FONT_SIZE_MENU = 20;

	private static final String TANK_TILE = "tileCamo.png";

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private float tiempoDificultad = 1f; // cada 15 segundos se actualiza
	private TiledTextureRegion mTankTexture;
	private TiledTextureRegion mBulletTexture;
	private TiledTextureRegion mZombieTexture;
	private ITextureRegion mBackLayer; // la parte de atras
	private ITextureRegion mFrontLayer; // los arbolitos
	private BitmapTextureAtlas mParallaxTexture;
	private Camera mCamera;
	private AnimatedSprite mTank;

	private Scene mScene;
	private PhysicsWorld mPhysicsWorld;
	private float mGravityX;
	private Font mFontHUD;
	private Font mFontMenu;
	// private float mGravityY;

	private Text text;

	private Game game = new Game(this);

	private boolean allowBulletCreation = true;

	private MenuScene mMenuScene;
	private GameTimerHandler mTimerOne;
	private GameTimerHandler mTimerTwo;

	private Sound tankFire; // Viene de
							// http://www.freesound.org/people/Cyberkineticfilms/sounds/127845/

	private Music tankRunning; // Viene de http://soundbible.com/1325-Tank.html

	private Sound zombieHit; // Viene de
								// http://soundbible.com/1033-Zombie-In-Pain.html

	private Sound tankHit; // Viene de http://soundbible.com/947-Metal-Bang.html

	private Music backgroundMusic;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, Game.CAMERA_WIDTH, Game.CAMERA_HEIGHT);
		EngineOptions options = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						Game.CAMERA_WIDTH, Game.CAMERA_HEIGHT), mCamera);
		options.getAudioOptions().setNeedsSound(true);
		options.getAudioOptions().setNeedsMusic(true);
		options.getAudioOptions().getSoundOptions()
				.setMaxSimultaneousStreams(10);
		return options;
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 500, 300, TextureOptions.BILINEAR);
		this.mTankTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						TANK_TILE, 5, 5, 6, 4);
		this.mBulletTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"bullet.png", 310, 5, 1, 1);
		this.mZombieTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"zombieP.png", 330, 5, 1, 1);
		this.mBitmapTextureAtlas.load();

		this.mParallaxTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 512, 1024);
		this.mBackLayer = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mParallaxTexture, this, "background.png",
						0, 0);
		this.mFrontLayer = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mParallaxTexture, this,
						"cactusLayer.png", 0, 512);
		this.mParallaxTexture.load();
		// Para la fuente

		ITexture fontTextureHUD = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		ITexture fontTextureMenu = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);

		FontFactory.setAssetBasePath("font/");
		this.mFontHUD = FontFactory.createFromAsset(this.getFontManager(),
				fontTextureHUD, this.getAssets(), "Adventure.ttf",
				FONT_SIZE_HUD, true, Color.BLACK.getARGBPackedInt());
		this.mFontMenu = FontFactory.createFromAsset(this.getFontManager(),
				fontTextureMenu, this.getAssets(), "Adventure.ttf",
				FONT_SIZE_MENU, true, Color.BLACK.getARGBPackedInt());
		this.mFontHUD.load();
		this.mFontMenu.load();

		try {

			tankFire = SoundFactory.createSoundFromAsset(getSoundManager(),
					this, "mfx/tankFire.ogg");
			zombieHit = SoundFactory.createSoundFromAsset(getSoundManager(),
					this, "mfx/zombieHit.ogg");
			tankHit = SoundFactory.createSoundFromAsset(getSoundManager(),
					this, "mfx/tankHit.ogg");
			tankRunning = MusicFactory.createMusicFromAsset(getMusicManager(),
					this, "mfx/tankRunning.ogg");
			backgroundMusic = MusicFactory.createMusicFromAsset(
					getMusicManager(), this, "mfx/backgroundMusic.ogg");

			if (getIntent().getExtras().getBoolean("sound")) {
				tankFire.setVolume(1f);
				zombieHit.setVolume(1f);
				tankRunning.setVolume(0.5f);
				backgroundMusic.setVolume(1f);
				tankHit.setVolume(1f);
			} else {
				tankFire.setVolume(0f);
				zombieHit.setVolume(0f);
				tankRunning.setVolume(0f);
				backgroundMusic.setVolume(0f);
				tankHit.setVolume(0f);
			}

		} catch (IOException e) {
			Log.e(TAG, "Sounds not loaded");
		}

	}

	@Override
	public Scene onCreateScene() {

		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_DEATH_STAR_I), false); // death star
																// gravity!!!!
		VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		// AutoParallaxBackground (float pRed, float pGreen, float pBlue, float
		// pParallaxChangePerSecond)
		AutoVerticalParallaxBackground autoParallaxBackground = new AutoVerticalParallaxBackground(
				0, 0, 0, 5);
		autoParallaxBackground
				.attachVerticalParallaxEntity(new VerticalParallaxEntity(-2.0f,
						new Sprite(0, Game.CAMERA_HEIGHT
								- this.mBackLayer.getHeight(), this.mBackLayer,
								vertexBufferObjectManager)));
		autoParallaxBackground
				.attachVerticalParallaxEntity(new VerticalParallaxEntity(-2.0f,
						new Sprite(0, Game.CAMERA_HEIGHT
								- this.mFrontLayer.getHeight(),
								this.mFrontLayer, vertexBufferObjectManager)));

		this.mScene = new Scene();
		this.mScene.setBackground(autoParallaxBackground);
		this.mScene.setOnSceneTouchListener(this);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		this.mScene.setOnAreaTouchListener(this);

		mPhysicsWorld.setContactListener(new GameContactListener(this, game));

		// Creadores de Zombies
		mTimerOne = new GameTimerHandler(DELAY_ZOMBIE, true,
				new GameITimerCallback(this));

		mTimerTwo = new GameTimerHandler(DELAY_ZOMBIE + 1, true,
				new GameITimerCallback(this));

		mEngine.registerUpdateHandler(mTimerOne);
		mEngine.registerUpdateHandler(mTimerTwo);

		mEngine.registerUpdateHandler(new TimerHandler(tiempoDificultad,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						mTimerOne.setTimerSeconds(mTimerOne.getTimerSeconds() * 0.95f);
						mTimerTwo.setTimerSeconds(mTimerTwo.getTimerSeconds() * 0.95f);
					}
				}));
		createRectangle();

		addTank(Game.CAMERA_WIDTH / 2 - TANK_WIDTH / 2, Game.CAMERA_HEIGHT
				- TANK_HEIGHT);

		text = new TickerText(10, 10, this.mFontHUD, REMAINING_LIFES_STRING
				+ game.getRemainingLife() + "\n" + SCORE_STRING
				+ game.getScore(), new TickerTextOptions(HorizontalAlign.LEFT,
				5), this.getVertexBufferObjectManager());

		this.mScene.attachChild(text);
		this.createMenuScene();

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
						tankFire.play();
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
		tankRunning.play();
		tankRunning.setLooping(true);
		backgroundMusic.play();
		backgroundMusic.setLooping(true);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		this.disableAccelerationSensor();
		tankRunning.stop();
		backgroundMusic.stop();
	}

	private void createRectangle() {
		int rectanglePX[] = { 0, 0, 0, Game.CAMERA_WIDTH - 2 };
		int rectanglePY[] = { Game.CAMERA_HEIGHT - 2, 0, 0, 0 };
		int retangleWidth[] = { Game.CAMERA_WIDTH, Game.CAMERA_WIDTH, 2, 2 };
		int rectangleHeigth[] = { 2, 2, Game.CAMERA_HEIGHT, Game.CAMERA_HEIGHT };
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

	@Override
	public void onBackPressed() {
		if (this.mScene.hasChildScene()) {
			mTimerOne.resume();
			mTimerTwo.resume();
			this.mScene.back();
			tankRunning.play();
			tankRunning.setLooping(true);
		} else {
			tankRunning.stop();
			mTimerOne.pause();
			mTimerTwo.pause();
			this.mScene.setChildScene(this.mMenuScene, false, true, true);
		}
	}

	private void addTank(final float pX, final float pY) {
		mTank = createAnimatedSprite(100f, pX, pY, mTankTexture,
				Game.TANK_TYPE, BodyType.DynamicBody);
		mTank.animate(100);
		((Body) mTank.getUserData()).setFixedRotation(true);
	}

	public void addZombie(final float pX, final float pY) {
		final AnimatedSprite zombie = createAnimatedSprite(0.1f, pX, pY,
				mZombieTexture, Game.NORMAL_ZOMBIE_TYPE, BodyType.KinematicBody);
		moveSprite(ZOMBIE_VELOCITY, (Body) zombie.getUserData());
	}

	private void addBullet() {
		float pX = mTank.getX() + TANK_WIDTH / 4;
		float pY = mTank.getY() - TANK_HEIGHT / 2;

		final AnimatedSprite bullet = createAnimatedSprite(0.1f, pX, pY,
				mBulletTexture, Game.BULLET_TYPE, BodyType.DynamicBody);

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
			float pY, TiledTextureRegion t, int type, BodyType bodyType) {

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(
				density, 0.1f, 0.5f);

		AnimatedSprite sprite = new AnimatedSprite(pX, pY, t,
				this.getVertexBufferObjectManager());

		final Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld,
				sprite, bodyType, objectFixtureDef);

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

	public void updateText() {
		text.setText(REMAINING_LIFES_STRING + game.getRemainingLife() + "\n"
				+ SCORE_STRING + game.getScore());
	}

	public void gameOver() {
		Intent intent = new Intent(this, GameOverActivity.class);
		intent.putExtra("puntuacion", (int) game.getScore());
		startActivity(intent);
		finish();
	}

	public void zombieKilled() {
		zombieHit.play();
	}

	public void tankHit() {
		tankHit.play();
	}
	
	public void mainMenu() {
		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
		finish();
	}

	public static class SpriteHolder {
		public AnimatedSprite sprite;
		public Body body;
		public int type; // 1 == bullet
	}

	protected void createMenuScene() {
		this.mMenuScene = new MenuScene(this.mCamera);

		final IMenuItem returnItemMenu = new ColorMenuItemDecorator(
				new TextMenuItem(REGRESAR_INICIO, this.mFontMenu,
						"Volver al inicio", this.getVertexBufferObjectManager()),
				new Color(1, 0, 0), new Color(0, 0, 0));
		returnItemMenu.setBlendFunction(GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(returnItemMenu);

		final IMenuItem quitMenuItem = new ColorMenuItemDecorator(
				new TextMenuItem(TERMINAR_PARTIDA, this.mFontMenu,
						"Terminar partida", this.getVertexBufferObjectManager()),
				new Color(1, 0, 0), new Color(0, 0, 0));
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);

		mMenuScene.addMenuItem(quitMenuItem);
		mMenuScene.buildAnimations();
		mMenuScene.setBackgroundEnabled(false);
		mMenuScene.setOnMenuItemClickListener(new ItemListener());

	}

	private class ItemListener implements IOnMenuItemClickListener {

		@Override
		public boolean onMenuItemClicked(MenuScene pMenuScene,
				IMenuItem pMenuItem, float pMenuItemLocalX,
				float pMenuItemLocalY) {
			switch (pMenuItem.getID()) {
			case REGRESAR_INICIO:
				mainMenu();
				break;
			case TERMINAR_PARTIDA:
				gameOver();
				break;
			}
			return false;
		}

	}

}
