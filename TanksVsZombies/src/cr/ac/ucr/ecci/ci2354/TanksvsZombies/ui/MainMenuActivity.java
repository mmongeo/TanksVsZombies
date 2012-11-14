package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class MainMenuActivity extends SherlockFragmentActivity {

	public static final String TABS[] = { "Menú Principal", "Puntajes Altos" };

	private MainMenuTabListener mTabListener;
	private MainMenuFragmentPagerAdapter mFragmentPagerAdapter = new MainMenuFragmentPagerAdapter(
			getSupportFragmentManager());
	private ViewPager mViewPager;
	private int mNumTabs = 2;
	private boolean soundEnabled = true;
	private MediaPlayer zombieHorde;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mViewPager = (ViewPager) findViewById(R.id.activity_main_pager);
		mViewPager.setAdapter(mFragmentPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						getSupportActionBar().setSelectedNavigationItem(
								position);
					}
				});

		mTabListener = new MainMenuTabListener(mViewPager);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (int i = 0; i < mNumTabs; ++i) {
			ActionBar.Tab t = getSupportActionBar().newTab();
			t.setText(TABS[i]);
			t.setTabListener(mTabListener);
			getSupportActionBar().addTab(t);
		}
		
		Boolean b = (Boolean) getLastCustomNonConfigurationInstance();
		if (b != null) {
			soundEnabled = b;
		}

		zombieHorde = MediaPlayer.create(this, R.raw.zombie_horde);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar_menu, menu);
		MenuItem i = menu.findItem(R.id.options_checkbox);
		i.setChecked(soundEnabled);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.options_checkbox:
			soundEnabled = !soundEnabled;
			item.setChecked(soundEnabled);
			if (soundEnabled) {
				Toast.makeText(getBaseContext(), "El sonido está activado",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "El sonido está desactivado",
						Toast.LENGTH_SHORT).show();
			}

			break;
		}

		return true;
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return soundEnabled;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		zombieHorde.seekTo(0);
		zombieHorde.start();
	}
	
	@Override
	protected void onPause() {
		super.onStop();
		zombieHorde.stop();
	}
	
	public boolean isSoundEnabled() {
		return soundEnabled;
	}
	
	private static class MainMenuTabListener implements ActionBar.TabListener {

		ViewPager viewPager;

		public MainMenuTabListener(ViewPager viewPager) {
			this.viewPager = viewPager;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			viewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

	}

	private static class MainMenuFragmentPagerAdapter extends
			FragmentPagerAdapter {

		public MainMenuFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			switch (position) {
			case 0:
				f = new MainMenuFragment();
				break;
			case 1:
				f = new HighScoresFragment();
				break;
			}
			return f;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
