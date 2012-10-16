package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.Services.ConnectServiceActivity;

public class GameOverActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
    }
    
    public void facebookShare(View view){
    	Intent intent = new Intent(getApplicationContext(),ConnectServiceActivity.class);
    	intent.putExtra("puntuacion", (int)(Math.random()*10000));//SE MANDA LA PUNTUACION
    	startActivity(intent);
    	finish();
    }

}
