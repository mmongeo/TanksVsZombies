package cr.ac.ucr.ecci.ci2354.TanksvsZombies.game;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.opengl.util.GLState;

public class VerticalParallaxBackground extends ParallaxBackground {
	public static int SCROLL_DOWN = -1;
	public static int SCROLL_UP = 1;

	private final ArrayList<VerticalParallaxEntity> mParallaxEntities = new ArrayList<VerticalParallaxEntity>();
	private int mParallaxEntityCount;

	protected float mParallaxValue;

	public VerticalParallaxBackground(float red, float green, float blue) {
		super(red, green, blue);
	}

	public void setParallaxValue(final float pParallaxValue) {
		this.mParallaxValue = pParallaxValue;
	}

	@Override
	public void onDraw(final GLState pGLState, final Camera pCamera) {
		super.onDraw(pGLState, pCamera);

		final float parallaxValue = this.mParallaxValue;
		final ArrayList<VerticalParallaxEntity> parallaxEntities = this.mParallaxEntities;
		for (int i = 0; i < this.mParallaxEntityCount; i++) {
			parallaxEntities.get(i).onDraw(pGLState, pCamera, parallaxValue);
		}
	}

	public void attachVerticalParallaxEntity(
			final VerticalParallaxEntity pParallaxEntity) {
		this.mParallaxEntities.add(pParallaxEntity);
		this.mParallaxEntityCount++;
	}

	public boolean detachVerticalParallaxEntity(
			final VerticalParallaxEntity pParallaxEntity) {
		this.mParallaxEntityCount--;
		final boolean success = this.mParallaxEntities.remove(pParallaxEntity);
		if (success == false) {
			this.mParallaxEntityCount++;
		}
		return success;
	}

	public static class VerticalParallaxEntity {

		final float mParallaxFactor;
		final IAreaShape mShape;
		private int direction;

		public VerticalParallaxEntity(final float pParallaxFactor,
				final IAreaShape pShape) {
			this.mParallaxFactor = pParallaxFactor;
			this.mShape = pShape;
			this.direction = VerticalParallaxBackground.SCROLL_DOWN;
		}

		public VerticalParallaxEntity(final float pParallaxFactor,
				final IAreaShape pShape, int direction) {
			this.mParallaxFactor = pParallaxFactor;
			this.mShape = pShape;
			this.direction = direction;
		}

		public void onDraw(final GLState pGL, final Camera pCamera,
				final float pParallaxValue) {

			pGL.pushModelViewGLMatrix();
			final float cameraHeight = pCamera.getHeight();
			final float shapeHeightScaled = this.mShape.getHeightScaled();
			float baseOffset = (pParallaxValue * this.mParallaxFactor)
					% shapeHeightScaled;
			while (baseOffset > 0) {
				baseOffset -= shapeHeightScaled;
			}
			pGL.translateModelViewGLMatrixf(0, (direction * baseOffset), 0);
			float currentMaxY = baseOffset;
			do {
				this.mShape.onDraw(pGL, pCamera);
				pGL.translateModelViewGLMatrixf(0,
						(direction * shapeHeightScaled), 0);
				currentMaxY += shapeHeightScaled;
			} while (currentMaxY < (cameraHeight + shapeHeightScaled));
			// Added shapeHeightScaled to cameraHeight so the drawing flows in
			// instead of popping in.

			pGL.popModelViewGLMatrix();

		}
	}
}
