package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class SplashActivity extends SherlockActivity {
	private Handler mHandler = new Handler();
	private ImageView mTittle;
	private Animation mAnimationSplash;

	public static final int DELAY_SPLASH = 3000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mTittle = (ImageView) findViewById(R.id.splash_image);
		mAnimationSplash = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.animacion_splash);
		mAnimationSplash.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mTittle.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mTittle.setVisibility(View.VISIBLE);
			}
		});
		mTittle.startAnimation(mAnimationSplash);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this,
						AnimationActivity.class));
				finish();

			}
		}, DELAY_SPLASH);
	}
}
