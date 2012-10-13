package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R.layout;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HighScoresActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_high_scores, menu);
        return true;
    }
}
