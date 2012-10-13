package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class MainMenuFragment extends SherlockFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_main_menu, null);
    }
    
}