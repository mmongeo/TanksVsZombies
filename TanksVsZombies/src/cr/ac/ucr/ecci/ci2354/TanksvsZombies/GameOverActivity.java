package cr.ac.ucr.ecci.ci2354.TanksvsZombies;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GameOverActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game_over, menu);
        return true;
    }
}
