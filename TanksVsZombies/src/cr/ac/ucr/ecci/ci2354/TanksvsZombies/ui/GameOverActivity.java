package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.j256.ormlite.dao.Dao;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.data.DBHelper;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.data.Score;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.services.ConnectServiceActivity;

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
		Intent intent = new Intent(GameOverActivity.this, ConnectServiceActivity.class);
		intent.putExtra("puntuacion", (int) score.getScore());
		startActivity(intent);
		// finish();
	}

}
