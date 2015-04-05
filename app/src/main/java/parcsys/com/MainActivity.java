package parcsys.com;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.UUID;

import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.fragment.SoldItemEditor;
import parcsys.com.fragment.StorageFragment;
import parcsys.com.marketfinal.R;
import parcsys.com.utils.DBHelper;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "tag";

    private FragmentTransaction fragmentTransaction;
    private StorageFragment storageFragment;
    private SoldItemEditor soldItemEditor;
    private Boolean isStorage;
    private Boolean onUserLeaveHint;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    final int DB_VERSION = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dbHelper = new DBHelper(this, DB_VERSION);
        db = dbHelper.getWritableDatabase();

        if (savedInstanceState != null) {
            isStorage = (Boolean) savedInstanceState.get("isStorage");
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.getStringExtra(SoldItemEditor.OK) != null) {
                SoldItem soldItem = intent.getParcelableExtra("currentItem");

                ContentValues cv = new ContentValues();
                cv.put("ID", soldItem.getId().toString());
                cv.put("AMOUNT", soldItem.getAmount());
                cv.put("PRICE", soldItem.getPrice());
                cv.put("TYPE", soldItem.getType().getId());
                cv.put("TITLE", soldItem.getTitle());

                db.beginTransaction();
                try {
                    db.insert("SOLD_ITEM_TABLE", null, cv);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                setIntent(null);
            }
        }

        if (isStorage == null || isStorage) {
            createStorage(savedInstanceState);
        } else {
            createEditor(savedInstanceState);
        }

        makeActionOverflowMenuShown();
    }

    private void createStorage(Bundle savedInstanceState) {
        storageFragment = new StorageFragment();
        Bundle bundle = new Bundle();
        List<SoldItem> soldItems = loadSoldItems();
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

    private List<SoldItem> loadSoldItems() {
        List<SoldItem> items = new ArrayList<SoldItem>();

        db.beginTransaction();
        try {
            Cursor cursor = db.query("SOLD_ITEM_TABLE", new String[]{"ID", "TITLE", "AMOUNT", "PRICE", "TYPE"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                    Integer amount = cursor.getInt(cursor.getColumnIndex("AMOUNT"));
                    Double price = cursor.getDouble(cursor.getColumnIndex("PRICE"));
                    String type = cursor.getString(cursor.getColumnIndex("TYPE"));

                    SoldItem soldItem = new SoldItem();
                    soldItem.setId(UUID.fromString(id));
                    soldItem.setType(SoldDestinationType.getById(type));
                    soldItem.setAmount(amount);
                    soldItem.setTitle(title);
                    soldItem.setPrice(price);

                    items.add(soldItem);

                    cursor.moveToNext();
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return items;
    }

    private void createEditor(@Nullable Bundle savedInstanceState) {
        soldItemEditor = new SoldItemEditor();

        if (savedInstanceState != null && savedInstanceState.get("currentItem") != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentItem", (Parcelable) savedInstanceState.get("currentItem"));
            soldItemEditor.setArguments(bundle);
        }

        clearFragmentStack();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, soldItemEditor);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_menu, menu);
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
                storageFragment = null;

                Toast.makeText(this, "Add item", Toast.LENGTH_SHORT)
                        .show();

                clearFragmentStack();
                createEditor(null);
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

    public void deleteSoldItem() {
        //Todo
    }

    public void updateSoldItem() {
        //Todo
    }
}
