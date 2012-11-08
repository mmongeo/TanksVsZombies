package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
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

public class GameOverActivity extends SherlockActivity {

	private static final String TAG = "GameOverActivity";

	Score score = new Score();
	Dao<Score, Integer> dao = null;
	EditText usernameEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);

		score.setScore(getIntent().getIntExtra("puntuacion", 0));

		TextView scoreTextView = (TextView) findViewById(R.id.game_over_score);
		usernameEditText = (EditText) findViewById(R.id.game_over_username_text);
		scoreTextView.setText("" + score.getScore());
		facebook = new Facebook(idApp);
		try {
			dao = DBHelper.getHelper().getDao(Score.class);
		} catch (Exception e) {
			Log.d(TAG, "Error creating DAO");
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		score.setUser(usernameEditText.getEditableText().toString());
		if (dao != null) {
			try {
				dao.createOrUpdate(score);
			} catch (Exception e) {
				Log.d(TAG, "Error updating DB");
			}
		}
	}

	public void facebookShare(View view) {
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		if (!facebook.isSessionValid()) {
			loginAndPost();
		} else {
			post((int) score.getScore());
		}
	}

	public static final String idApp = "413285808737731";
	public static final String TOKEN = "access_token";
	public static final String EXPIRES = "expires_in";
	public static final String KEY = "facebook-credentials";

	Facebook facebook;
	AsyncFacebookRunner mAsyncRunner;

	private void post(int puntuacion) {
		// TODO Auto-generated method stub
		Bundle parameters = new Bundle();
		parameters.putString("message", "He conseguido una puntuacion de "
				+ puntuacion + " en el juego TANKS vs ZOMBIES");
		parameters.putString("description", "Nueva puntuacion");
		parameters.putString("picture", "http://i49.tinypic.com/2a9d3td.png");

		try {
			// mAsyncRunner.request("me", null);
			mAsyncRunner.request("me/feed", parameters, "POST",
					new RequestListener() {

						@Override
						public void onMalformedURLException(
								MalformedURLException e, Object state) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onIOException(IOException e, Object state) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFileNotFoundException(
								FileNotFoundException e, Object state) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFacebookError(FacebookError e,
								Object state) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onComplete(String response, Object state) {
							// TODO Auto-generated method stub

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
			// TODO Auto-generated method stub
			saveCredentials(facebook);
			post((int) score.getScore());
		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			showToast("Autenticacion con facebook fallo " + e.getMessage());
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			showToast("Autenticacion con facebook fallo " + e.getMessage());

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			showToast("Autenticacion con facebook cancelada");
		}
	}

	private void showToast(String msj) {
		Toast.makeText(GameOverActivity.this, msj, Toast.LENGTH_LONG).show();
	}

}
