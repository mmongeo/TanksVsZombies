package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
 /*
  * http://bhamdevelopment.blogspot.com/2012/08/andengine-extensions-pausable-timer.html
  * Este codigo fue copiado del link anterior.
  * */
public class PauseableTimerHandler extends TimerHandler {

	private boolean mPause = false;

	public PauseableTimerHandler(float pTimerSeconds, boolean pAutoReset,
			ITimerCallback pTimerCallback) {
		super(pTimerSeconds, pAutoReset, pTimerCallback);
	}

	public void pause() {
		this.mPause = true;
	}

	public void resume() {
		this.mPause = false;
	}

	@Override
	public void onUpdate(float pSecondsElapsed) {
		if (!this.mPause) {
			super.onUpdate(pSecondsElapsed);
		}
	}
}