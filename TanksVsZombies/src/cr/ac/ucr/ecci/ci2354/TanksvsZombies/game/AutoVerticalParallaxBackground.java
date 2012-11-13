package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

public class AutoVerticalParallaxBackground extends VerticalParallaxBackground {

	private boolean paused;
	private final float mParallaxChangePerSecond;

	public AutoVerticalParallaxBackground(final float pRed, final float pGreen,
			final float pBlue, final float pParallaxChangePerSecond) {
		super(pRed, pGreen, pBlue);
		this.mParallaxChangePerSecond = pParallaxChangePerSecond;
		this.paused = false;
	}

	public void setPaused(boolean b) {
		this.paused = b;

	}

	@Override
	public void onUpdate(final float pSecondsElapsed) {
		if (!this.paused) {
			super.onUpdate(pSecondsElapsed);

			this.mParallaxValue += this.mParallaxChangePerSecond
					* pSecondsElapsed;
		}
	}

}