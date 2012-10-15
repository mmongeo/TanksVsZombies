package cr.ac.ucr.ecci.ci2354.TanksvsZombies.data;

import com.j256.ormlite.field.DatabaseField;

public class Score {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String user;

	@DatabaseField
	private long score;

	public Score() {
	}

	public Score(String user, long score) {
		this.user = user;
		this.score = score;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		sb.append("id = ").append(id).append(", ");
		sb.append("user = ").append(user).append(", ");
		sb.append("score = ").append(score);
		sb.append(" ]");
		return sb.toString();
	}

}
