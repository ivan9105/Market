package parcsys.com;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import parcsys.com.adapters.SoldItemWrapper;
import parcsys.com.entity.SoldItem;
import parcsys.com.fragment.SoldItemEditor;
import parcsys.com.fragment.StorageFragment;
import parcsys.com.marketfinal.R;
import parcsys.com.utils.DBHelper;
import parcsys.com.utils.DaoStaticUtils;
import parcsys.com.utils.dao.Dao;
import parcsys.com.utils.dao.sold_item_dao.SoldItemJSONDao;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "tag";

    private FragmentTransaction fragmentTransaction;
    private StorageFragment storageFragment;
    private SoldItemEditor soldItemEditor;
    private Boolean isStorage;
    private Boolean onUserLeaveHint;

    private Dao<SoldItem> dao;

    final int DB_VERSION = 12;

    private Menu menu;

    public Map<Integer, DisableBuyAction> buyTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DBHelper dbHelper = new DBHelper(this, DB_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dao = new SoldItemJSONDao(db);
        initDaoStaticUtil();

        if (savedInstanceState != null) {
            isStorage = (Boolean) savedInstanceState.get("isStorage");
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.getStringExtra(SoldItemEditor.OK) != null) {
                SoldItem soldItem = intent.getParcelableExtra("currentItem");

                List<SoldItem> items = dao.getItems();
                boolean modified = false;
                for (SoldItem item : items) {
                    if (item.getTitle().equals(soldItem.getTitle())) {
                        item.setAmount(item.getAmount() + soldItem.getAmount());
                        dao.updateItem(item);

                        modified = true;
                    }
                }

                if (!modified) {
                    dao.addItem(soldItem);
                }

                setIntent(null);
            }
        }

        if (isStorage == null || isStorage) {
            createStorage(savedInstanceState);
        } else {
            clearFragmentStack();
            createEditor(savedInstanceState);
        }

        makeActionOverflowMenuShown();
    }

    private void initDaoStaticUtil() {
        DaoStaticUtils daoStaticUtils = new DaoStaticUtils(dao);
    }

    private void createStorage(Bundle savedInstanceState) {
        storageFragment = new StorageFragment();
        Bundle bundle = new Bundle();
        List<SoldItem> soldItems = dao.getItems();

        List<SoldItemWrapper> wrappers = new ArrayList<SoldItemWrapper>();
        for (SoldItem item : soldItems) {
            wrappers.add(new SoldItemWrapper(item, true));
        }

        if (savedInstanceState != null && savedInstanceState.get("currentPosition") != null) {
            bundle.putInt("currentPosition", (Integer) savedInstanceState.get("currentPosition"));
        }
        bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) wrappers);
        storageFragment.setArguments(bundle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, storageFragment, StorageFragment.NAME);
        fragmentTransaction.commit();
        isStorage = true;

        storageFragment.setActivity(this);
    }

    private void createEditor(@Nullable Bundle savedInstanceState) {
        soldItemEditor = new SoldItemEditor();

        if (savedInstanceState != null && savedInstanceState.get("currentItem") != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentItem", (Parcelable) savedInstanceState.get("currentItem"));
            soldItemEditor.setArguments(bundle);
        }

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, soldItemEditor, SoldItemEditor.NAME);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_menu, menu);
        this.menu = menu;
        if (!isStorage) {
            menu.findItem(R.id.addItem).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.card:
                Toast.makeText(this, "card", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.addItem:
                isStorage = false;
                Toast.makeText(this, "Add item", Toast.LENGTH_SHORT)
                        .show();

                storageFragment = null;
                clearFragmentStack();
                createEditor(null);

                menu.findItem(R.id.addItem).setVisible(false);
            default:
                break;
        }

        return true;
    }

    private void clearFragmentStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }

        if (storageFragment != null && onUserLeaveHint == null) {
            outState.putInt("currentPosition", storageFragment.getListView().getFirstVisiblePosition());
            storageFragment = null;
        }

        if (soldItemEditor != null) {
            outState.putParcelable("currentItem", soldItemEditor.getCurrentItem());
        }

        outState.putBoolean("isStorage", isStorage);

        super.onSaveInstanceState(outState);

        onUserLeaveHint = null;
    }

    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    public void createDisableBuyAction(UUID uuid) {
        DisableBuyAction disableBuyAction = new DisableBuyAction(uuid, storageFragment);
        disableBuyAction.execute();
    }

    private class DisableBuyAction extends AsyncTask<Void, Void, Void> {
        private UUID uuid;
        private StorageFragment storage;

        DisableBuyAction(UUID uuid, StorageFragment storage) {
            this.uuid = uuid;
            this.storage = storage;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 1; i <= 7; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    Log.d("qwe", "i = " + i
                            + ", MyTask: " + this.hashCode()
                            + ", MainActivity: " + MainActivity.this.hashCode());
                }
                onProgressUpdate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (storage != null) {
                ListAdapter adapter = storageFragment.getListAdapter();

                SoldItemWrapper wrapper = null;

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (((SoldItemWrapper) adapter.getItem(i)).getSoldItem().getId().equals(uuid)) {
                        wrapper = (SoldItemWrapper) adapter.getItem(i);
                    }
                }
                if (wrapper != null) {
                    wrapper.setEnable(true);
                    synchronized (adapter) {
                        ((ArrayAdapter) adapter).notifyDataSetChanged();
                    }
                }
            }
        }
    }
}
