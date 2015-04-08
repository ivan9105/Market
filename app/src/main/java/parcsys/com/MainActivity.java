package parcsys.com;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import parcsys.com.entity.SoldItem;
import parcsys.com.fragment.SoldItemEditor;
import parcsys.com.fragment.StorageFragment;
import parcsys.com.marketfinal.R;
import parcsys.com.utils.DBHelper;
import parcsys.com.utils.DaoStaticUtils;
import parcsys.com.utils.dao.Dao;
import parcsys.com.utils.dao.sold_item_dao.SoldItemDbDao;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "tag";

    private FragmentTransaction fragmentTransaction;
    private StorageFragment storageFragment;
    private SoldItemEditor soldItemEditor;
    private Boolean isStorage;
    private Boolean onUserLeaveHint;

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Dao<SoldItem> dao;

    final int DB_VERSION = 8;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dbHelper = new DBHelper(this, DB_VERSION);
        db = dbHelper.getWritableDatabase();
        dao = new SoldItemDbDao(db);
        initDaoStaticUtil();

        if (savedInstanceState != null) {
            isStorage = (Boolean) savedInstanceState.get("isStorage");
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.getStringExtra(SoldItemEditor.OK) != null) {
                SoldItem soldItem = intent.getParcelableExtra("currentItem");
                dao.addItem(soldItem);
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
        if (savedInstanceState != null && savedInstanceState.get("currentPosition") != null) {
            bundle.putInt("currentPosition", (Integer) savedInstanceState.get("currentPosition"));
        }
        bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) soldItems);
        storageFragment.setArguments(bundle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame, storageFragment);
        fragmentTransaction.commit();

        isStorage = true;
    }

    private void createEditor(@Nullable Bundle savedInstanceState) {
        soldItemEditor = new SoldItemEditor();

        if (savedInstanceState != null && savedInstanceState.get("currentItem") != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentItem", (Parcelable) savedInstanceState.get("currentItem"));
            soldItemEditor.setArguments(bundle);
        }

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, soldItemEditor);
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
    protected void onUserLeaveHint() {
        onUserLeaveHint = true;
        super.onUserLeaveHint();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }

        if (storageFragment != null && onUserLeaveHint == null) {
            outState.putInt("currentPosition", storageFragment.getListView().getFirstVisiblePosition());
            getSupportFragmentManager().beginTransaction().
                    remove(storageFragment).
                    commit();
            storageFragment = null;
        }

        if (soldItemEditor != null) {
            outState.putParcelable("currentItem", soldItemEditor.getCurrentItem());
            if (onUserLeaveHint == null) {
                getSupportFragmentManager().beginTransaction().
                        remove(soldItemEditor).
                        commit();
                soldItemEditor = null;
            }
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
}
