package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.j256.ormlite.dao.Dao;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.data.DBHelper;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.data.Score;

public class GameOverActivity extends SherlockFragmentActivity {

	private static final String TAG = "GameOverActivity";

	public static final String ID_APP = "413285808737731";
	public static final String TOKEN = "access_token";
	public static final String EXPIRES = "expires_in";
	public static final String KEY = "facebook-credentials";

	private static final String USER_DIALOG = "UserDialog";
	private boolean firstResume = true;

	private Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);
		facebook = new Facebook(ID_APP);
		
		Boolean b = (Boolean) getLastCustomNonConfigurationInstance();
		if (b != null) {
			firstResume = b;
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		UserDialogFragment u = (UserDialogFragment) getSupportFragmentManager()
				.findFragmentByTag(USER_DIALOG);
		if (u != null) {
			u.dettach();
		}
	}

	public void facebookShare(View view) {
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		if (!facebook.isSessionValid()) {
			loginAndPost();
		} else {
			post(getIntent().getIntExtra("puntuacion", 0));
		}
	}

	public void finishGame(View view) {
		startActivity(new Intent(getApplicationContext(), MainMenuActvity.class));
		finish();
	}

	private void post(int puntuacion) {
		// TODO Auto-generated method stub
		Bundle parameters = new Bundle();
		parameters.putString("message", "He conseguido una puntuacion de "
				+ puntuacion + " en el juego TANKS vs ZOMBIES");
		parameters.putString("description", "Nueva puntuacion");
		parameters.putString("picture", "http://i49.tinypic.com/2a9d3td.png");

		try {
			mAsyncRunner.request("me/feed", parameters, "POST",
					new RequestListener() {

						@Override
						public void onMalformedURLException(
								MalformedURLException e, Object state) {
						}

						@Override
						public void onIOException(IOException e, Object state) {
						}

						@Override
						public void onFileNotFoundException(
								FileNotFoundException e, Object state) {
						}

						@Override
						public void onFacebookError(FacebookError e,
								Object state) {
						}

						@Override
						public void onComplete(String response, Object state) {
						}
					}, null);
			Log.d("", "SE LOGRO RESPUESTA ");
			showToast("Se realizo con exito la publicacion del puntaje en tu muro");
		} catch (Exception e) {
			showToast("Fallo la publicacion del puntaje en tu muro");
		}

	}

	private void loginAndPost() {
		facebook.authorize(this, new String[] { "publish_stream",
				"publish_actions", "user_games_activity",
				"friends_games_activity" }, Facebook.FORCE_DIALOG_AUTH,
				new LoginDialogListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		facebook.extendAccessToken(this, null);

		if (firstResume) {
			firstResume = false;
			DialogFragment d = UserDialogFragment.newInstance(this);
			d.show(getSupportFragmentManager(), USER_DIALOG);
		} else {
			UserDialogFragment u = (UserDialogFragment) getSupportFragmentManager()
					.findFragmentByTag(USER_DIALOG);
			if (u != null) {
				u.attach(this);
			}
		}
	}

	public boolean saveCredentials(Facebook fac) {
		Editor editor = GameOverActivity.this.getSharedPreferences(KEY,
				Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, fac.getAccessToken());
		editor.putLong(EXPIRES, fac.getAccessExpires());
		return editor.commit();

	}

	public boolean restoreCredentials(Facebook fac) {
		SharedPreferences shared = GameOverActivity.this.getSharedPreferences(
				KEY, Context.MODE_PRIVATE);
		fac.setAccessToken(shared.getString(TOKEN, null));
		fac.setAccessExpires(shared.getLong(EXPIRES, 0));
		return fac.isSessionValid();
	}

	class LoginDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			post(getIntent().getIntExtra("puntuacion", 0));
		}

		@Override
		public void onFacebookError(FacebookError e) {
			showToast("Autenticacion con facebook fallo " + e.getMessage());
		}

		@Override
		public void onError(DialogError e) {
			showToast("Autenticacion con facebook fallo " + e.getMessage());
		}

		@Override
		public void onCancel() {
			showToast("Autenticacion con facebook cancelada");
		}
	}

	private void showToast(String msj) {
		Toast.makeText(GameOverActivity.this, msj, Toast.LENGTH_LONG).show();
	}

	public void userNameChoosed(String user) {

		Score score = new Score();
		score.setScore(getIntent().getIntExtra("puntuacion", 0));
		score.setUser(user);

		TextView scoreTextView = (TextView) findViewById(R.id.game_over_score);
		scoreTextView.setText("" + score.getScore());

		TextView userTextView = (TextView) findViewById(R.id.game_over_username_text);
		userTextView.setText(user);

		try {
			Dao<Score, Integer> dao = DBHelper.getHelper().getDao(Score.class);
			dao.createOrUpdate(score);
		} catch (Exception e) {
			Log.d(TAG, "Error updating DB");
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return firstResume;
	}

}
