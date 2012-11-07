package cr.ac.ucr.ecci.ci2354.TanksvsZombies.data;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.App;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class DBHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = "DB";
	private static final String DATABASE_NAME = "tanks_vs_zombies.db";
	private static final int DATABASE_VERSION = 1;
	private static DBHelper helper = new DBHelper(App.getAppContext());

	public static DBHelper getHelper() {
		return helper;
	}
	
	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

		Log.d(TAG, "onCreate");

		try {
			TableUtils.createTable(connectionSource, Score.class);
		} catch (SQLException e) {
			Log.e(TAG, "Can't create database", e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {

	}



}
