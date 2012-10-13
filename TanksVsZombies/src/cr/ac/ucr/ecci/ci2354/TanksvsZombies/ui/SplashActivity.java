package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R.layout;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R.menu;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash, menu);
        return true;
    }
}
