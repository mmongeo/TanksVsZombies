package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class GameActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
}