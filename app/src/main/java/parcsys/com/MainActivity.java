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

import parcsys.com.adapters.SoldItemAdapter;
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
    private int currentPosition = 0;
    List<SoldItem> disabledItems = new ArrayList<SoldItem>();
//    private Boolean onUserLeaveHint;

    private Dao<SoldItem> dao;

    final int DB_VERSION = 12;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DBHelper dbHelper = new DBHelper(this, DB_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dao = new SoldItemJSONDao(db);
        initDaoStaticUtil();

        createStorage();
        //Todo избавиться от new intent во item editor от saved state
        //Todo с фрагментами будет динамическая работа изначально будет storage чтобы activity не пересоздавалось
        //изменять начиная отсюда
        /*
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
            createEditor(savedInstanceState);
        }
        */

        makeActionOverflowMenuShown();
    }

    private void initDaoStaticUtil() {
        new DaoStaticUtils(dao);
    }

    public void createStorage() {
        storageFragment = new StorageFragment();
        Bundle bundle = new Bundle();
        List<SoldItem> soldItems = dao.getItems();

        List<SoldItemWrapper> wrappers = new ArrayList<SoldItemWrapper>();
        for (SoldItem item : soldItems) {
            if (!disabledItems.contains(item)) {
                wrappers.add(new SoldItemWrapper(item, true));
            } else {
                wrappers.add(new SoldItemWrapper(item, false));
            }
        }

        if (currentPosition != 0) {
            bundle.putInt("currentPosition", (Integer) currentPosition);
        }
        bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) wrappers);
        storageFragment.setArguments(bundle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, storageFragment, StorageFragment.NAME);
        fragmentTransaction.commit();
        isStorage = true;

        storageFragment.setActivity(this);

        if (menu != null && !menu.findItem(R.id.addItem).isVisible()) {
            menu.findItem(R.id.addItem).setVisible(true);
        }
    }

    private void createEditor() {
        soldItemEditor = new SoldItemEditor();
        soldItemEditor.setMainActivity(this);
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
                createEditor();

                menu.findItem(R.id.addItem).setVisible(false);
            default:
                break;
        }

        return true;
    }

/*    @Override
    protected void onUserLeaveHint() {
        onUserLeaveHint = true;
        super.onUserLeaveHint();
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /*if (outState == null) {
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

        onUserLeaveHint = null;*/
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

    public void createDisableBuyAction(SoldItem item) {
        DisableBuyAction disableBuyAction = new DisableBuyAction(item, this);
        disableBuyAction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        disabledItems.add(item);
    }

    public void addItemAction(SoldItem soldItem) {
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
    }

    private StorageFragment getStorage() {
        return storageFragment;
    }

    private class DisableBuyAction extends AsyncTask<Void, Void, Void> {
        private SoldItem item;
        private MainActivity activity;

        DisableBuyAction(SoldItem item, MainActivity activity) {
            this.item = item;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 1; i <= 15; i++) {
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
            if (activity != null) {
                if (activity.getStorage() != null) {
                    ListAdapter adapter = activity.getStorage().getListAdapter();

                    SoldItemWrapper wrapper = null;

                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (((SoldItemWrapper) adapter.getItem(i)).getSoldItem().getId().equals(item.getId())) {
                            wrapper = (SoldItemWrapper) adapter.getItem(i);
                        }
                    }
                    if (wrapper != null) {
                        if (item.getAmount() - 1 > 0) {
                            item.setAmount(item.getAmount() - 1);
                            DaoStaticUtils.getDao().updateItem(item);
                        } else {
                            ((SoldItemAdapter) adapter).remove(wrapper);
                            DaoStaticUtils.getDao().removeItem(item);
                        }
                        wrapper.setEnable(true);

                        synchronized (adapter) {
                            ((ArrayAdapter) adapter).notifyDataSetChanged();
                        }
                    }
                } else {
                    dao.buyItem(item);
                }

                disabledItems.remove(item);
            }
        }
    }
}
