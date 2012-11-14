package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

import java.lang.ref.WeakReference;

public class Game {

	// Tamaño de la pantalla
	public static final int CAMERA_WIDTH = 360;
	public static final int CAMERA_HEIGHT = 240;
	
	// Tamaño zombies
	public static final int ZOMBIE_WIDTH = 24;

	// Tipos de objetos en pantalla
	public static final int ANY_TYPE = 0;
	public static final int TANK_TYPE = 1;
	public static final int BULLET_TYPE = 2;
	public static final int NORMAL_ZOMBIE_TYPE = 3;

	public static final int NORMAL_ZOMBIE_KILL = 50;

	private WeakReference<GameActivity> weakReference;
	private int score = 0;
	private int lifes = 3;

	public Game(GameActivity game) {
		attach(game);
	}

	public void attach(GameActivity game) {
		weakReference = new WeakReference<GameActivity>(game);
	}

	public void dettach() {
		weakReference = null;
	}

	public void increaseScore(int points) {
		score += points;
		weakReference.get().updateText();
		weakReference.get().zombieKilled();
	}

	public void decreaseLife() {
		--lifes;
		weakReference.get().updateText();
		weakReference.get().tankHit();
		if (lifes == 0) {
			weakReference.get().gameOver();
		}
	}

	public int getScore() {
		return score;
	}

	public int getRemainingLife() {
		return lifes;
	}

}
