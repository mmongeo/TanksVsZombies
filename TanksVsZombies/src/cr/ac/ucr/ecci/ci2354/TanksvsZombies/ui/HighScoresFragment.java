package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.j256.ormlite.dao.Dao;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.data.DBHelper;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.data.Score;

public class HighScoresFragment extends SherlockListFragment {

	private static final String TAG = "High Scores Fragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

			// IMPORTANTE!!!!
			// REVISAR CUAL ES EL EQUIVALENTE DE ONRETAINCSUTOMCONFIGURATIONINSTANCE PARA FRAGMENTOS
			
			Dao<Score, Integer> scoreDao = DBHelper.getHelper().getDao(Score.class);
			ScoreAdapter adapter = new ScoreAdapter(getActivity().getApplicationContext());
			adapter.setList(scoreDao.queryForAll());
			setListAdapter(adapter);

		} catch (SQLException e) {
			Log.e(TAG, "Couldn't create adapter", e);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_high_scores, null);
	}

	private static class ScoreAdapter extends BaseAdapter {

		List<Score> scores = new ArrayList<Score>();
		LayoutInflater inflater;

		public ScoreAdapter(Context context) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return scores.size();
		}

		@Override
		public Object getItem(int position) {
			return scores.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Score score = (Score) getItem(position);
			ScoreHolder holder = null;

			if (convertView == null) {

				convertView = inflater.inflate(R.layout.fragment_high_scores_row_layout, parent, false);
				holder = new ScoreHolder();
				convertView.setTag(holder);

				holder.user = (TextView) convertView.findViewById(R.id.fragment_high_scores_row_layout_user);
				holder.score = (TextView) convertView.findViewById(R.id.fragment_high_scores_row_layout_score);

			} else {
				holder = (ScoreHolder) convertView.getTag();
			}

			holder.user.setText(score.getUser());
			holder.score.setText(String.valueOf(score.getScore()));

			return convertView;
		}

		public void setList(List<Score> scores) {
			this.scores = scores;
			notifyDataSetChanged();
		}
	}

	private static class ScoreHolder {
		public TextView user;
		public TextView score;
	}

}
