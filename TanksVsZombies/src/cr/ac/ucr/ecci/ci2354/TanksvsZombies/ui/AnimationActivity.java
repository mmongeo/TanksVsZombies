package cr.ac.ucr.ecci.ci2354.TanksvsZombies;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class AnimationActivity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_animation, menu);
		return true;
	}
}
