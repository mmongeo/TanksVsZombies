package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui.gameStore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class StoreItemDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeitem_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(StoreItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(StoreItemDetailFragment.ARG_ITEM_ID));
            StoreItemDetailFragment fragment = new StoreItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.storeitem_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, StoreItemListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
