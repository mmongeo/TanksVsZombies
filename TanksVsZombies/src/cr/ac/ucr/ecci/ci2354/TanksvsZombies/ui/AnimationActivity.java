package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class AnimationActivity extends Activity {
	TextView mMensaje;
	Animation mAnimacionTexto;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation);
		mMensaje = (TextView) findViewById(R.id.animation_text_continuar);
		mAnimacionTexto = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animacion_texto);
		mAnimacionTexto.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mMensaje.startAnimation(mAnimacionTexto);
				
			}
		});
		mMensaje.startAnimation(mAnimacionTexto);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		animateImage(R.id.animation_tank, R.anim.animacion_tanque);
		animateImage(R.id.animation_zombie, R.anim.animacion_zombie);
	}

	public void animateImage(int idView, int idAnim) {
		ImageView imageView = (ImageView) findViewById(idView);
		imageView.setImageResource(idAnim);
		// Get the background, which has been compiled to an AnimationDrawable
		// object.
		AnimationDrawable anim = (AnimationDrawable) imageView.getDrawable();
		anim.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			startActivity(new Intent(getApplicationContext(), MainMenu.class));
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_animation, menu);
		return true;
	}
}
