package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

import java.lang.ref.WeakReference;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

public class GameITimerCallback implements ITimerCallback {

	private WeakReference<GameActivity> weakReference;

	public GameITimerCallback(GameActivity activity) {
		attach(activity);
	}

	@Override
	public void onTimePassed(TimerHandler pTimerHandler) {
		weakReference.get().addZombie(
				(float) (Math.random() * (Game.CAMERA_WIDTH - Game.ZOMBIE_WIDTH)), 0f);
	}

	public void attach(GameActivity activity) {
		weakReference = new WeakReference<GameActivity>(activity);
	}

	public void dettach() {
		weakReference = null;
	}
}
