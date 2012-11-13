package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

import java.lang.ref.WeakReference;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.game.GameActivity.SpriteHolder;

class GameContactListener implements ContactListener {

	private WeakReference<GameActivity> weakReference;
	private Game game;

	private static final int NUMBER_OF_CASES = 6;

	// Casos de colisión
	// Cualquier colisión que lleve ANY debe ir de última
	private static final int typeA[] = { Game.NORMAL_ZOMBIE_TYPE,
			Game.BULLET_TYPE, Game.TANK_TYPE, Game.NORMAL_ZOMBIE_TYPE,
			Game.BULLET_TYPE, Game.ANY_TYPE };
	private static final int typeB[] = { Game.BULLET_TYPE,
			Game.NORMAL_ZOMBIE_TYPE, Game.NORMAL_ZOMBIE_TYPE, Game.TANK_TYPE,
			Game.ANY_TYPE, Game.BULLET_TYPE };
	private static final boolean killA[] = { true, true, false, true, true,
			false };
	private static final boolean killB[] = { true, true, true, false, false,
			true };

	// Tipo de cada colisión
	private static final int NZOMBIE_BULLET = 0;
	private static final int BULLET_NZOMBIE = 1;
	private static final int TANK_NZOMBIE = 2;
	private static final int NZOMBIE_TANK = 3;
	private static final int BULLET_ANY = 4;
	private static final int ANY_BULLET = 5;

	public GameContactListener(GameActivity gameActivity, Game game) {
		attach(gameActivity);
		this.game = game;
	}

	public void attach(GameActivity game) {
		weakReference = new WeakReference<GameActivity>(game);
	}

	public void dettach() {
		weakReference = null;
	}

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

		Body bodyVector[] = { contact.getFixtureA().getBody(),
				contact.getFixtureB().getBody() };
		SpriteHolder spriteVector[] = { null, null };
		for (int i = 0; i < 2; ++i) {
			if ((SpriteHolder) bodyVector[i].getUserData() != null) {
				spriteVector[i] = (SpriteHolder) bodyVector[i].getUserData();
			}
		}

		verifyCases(spriteVector[0], spriteVector[1]);
	}

	// permite nulos en los SpriteHolder
	private void verifyCases(SpriteHolder a, SpriteHolder b) {

		int collisionType = -1;

		for (int i = 0; i < NUMBER_OF_CASES && collisionType == -1; ++i) {
			if (verifyTypes(a, b, typeA[i], typeB[i])) {
				killSprites(a, b, killA[i], killB[i]);
				collisionType = i;
			}
		}

		switch (collisionType) {
		case BULLET_NZOMBIE:
		case NZOMBIE_BULLET:
			game.increaseScore(Game.NORMAL_ZOMBIE_KILL);
			break;
		case TANK_NZOMBIE:
		case NZOMBIE_TANK:
			game.decreaseLife();
			break;
		default:
		case BULLET_ANY:
		case ANY_BULLET:
			break;
		}

	}

	// permite nulos en los SpriteHolder
	private boolean verifyTypes(SpriteHolder a, SpriteHolder b, int typeA,
			int typeB) {
		return (a != null && a.type == typeA || typeA == Game.ANY_TYPE)
				&& (b != null && b.type == typeB || typeB == Game.ANY_TYPE);
	}

	private void killSprites(final SpriteHolder a, final SpriteHolder b,
			final boolean killA, final boolean killB) {
		weakReference.get().getEngine().runOnUpdateThread(new Runnable() {
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

}