package cr.ac.ucr.ecci.ci2354.TanksvsZombies.ui.gameStore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cr.ac.ucr.ecci.ci2354.TanksvsZombies.R;

public class StoreItemListActivity extends FragmentActivity
        implements StoreItemListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeitem_list);

        if (findViewById(R.id.storeitem_detail_container) != null) {
            mTwoPane = true;
            ((StoreItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.storeitem_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(StoreItemDetailFragment.ARG_ITEM_ID, id);
            StoreItemDetailFragment fragment = new StoreItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.storeitem_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, StoreItemDetailActivity.class);
            detailIntent.putExtra(StoreItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
