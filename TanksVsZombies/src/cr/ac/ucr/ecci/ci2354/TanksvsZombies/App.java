package cr.ac.ucr.ecci.ci2354.TanksvsZombies;

import android.app.Application;
import android.content.Context;

public class App extends Application {

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		App.context = getApplicationContext();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Context getAppContext() {
		return App.context;
	}
}
