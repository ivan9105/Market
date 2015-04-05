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
    private List<SoldItem> testData = new ArrayList<SoldItem>();
    //    private Boolean isCreated;
//    private Integer currentPosition;
    private Boolean isStorage;
    //    private Boolean isClear;
    private Boolean onUserLeaveHint;

//    private SoldItem currentItem;

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
        /*restoreData(savedInstanceState);

        if (isCreated == null) {
            addTestData();
            isCreated = true;
            isClear = false;
        }

        checkIsStorage();

        if (isStorage == null || isStorage) {
            createStorage();
        } else {
            createEditor();
        }

        makeActionOverflowMenuShown();*/
    }

    private void checkIsStorage() {
//        Intent intent = getIntent();
//        if (!isClear && intent != null && (intent.getStringExtra(SoldItemEditor.OK) != null
//                || intent.getStringExtra(SoldItemEditor.CANCEL) != null)) {
//            restoreData(intent.getExtras());
//            isStorage = true;
//            currentItem = intent.getExtras().getParcelable("currentItem");
//            if (currentItem != null) {
//                //skip logic of adding - it simple
//                testData.add(currentItem);
//                intent.removeExtra("currentItem");
//            }
//            intent.removeExtra(SoldItemEditor.OK);
//            intent.removeExtra(SoldItemEditor.CANCEL);
//        }
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

        /*if (currentPosition != null) {
            bundle.putInt("currentPosition", currentPosition);
        }
        storageFragment.setArguments(bundle);

        if (!isClear) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragmentFrame, storageFragment);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentFrame, storageFragment);
            fragmentTransaction.commit();
        }
        isStorage = true;
        isClear = false;*/
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

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, soldItemEditor);
        fragmentTransaction.commit();
    }

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState == null || isStorage != null && !isStorage) {
            Intent intent = getIntent();
//            if (intent.getStringExtra(SoldItemEditor.OK) != null) {
//                currentItem = intent.getParcelableExtra("currentItem");
//                testData.add(currentItem);
//            }
            return;
        }

        isStorage = (Boolean) savedInstanceState.get("isStorage");
//        isCreated = (Boolean) savedInstanceState.get("isCreated");
//        isClear = (Boolean) savedInstanceState.get("isClear");
//
//        if (savedInstanceState.get("items") != null) {
//            testData = savedInstanceState.getParcelableArrayList("items");
//        }
//
//        if (savedInstanceState.get("currentItem") != null) {
//            currentItem = savedInstanceState.getParcelable("currentItem");
//        }
//
//        currentPosition = savedInstanceState.getInt("currentPosition");
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

//        outState.putBoolean("isCreated", isCreated);
        /*createBundle(outState, false);

        if (storageFragment != null && onUserLeaveHint == null) {
            getSupportFragmentManager().beginTransaction().
                    remove(storageFragment).
                    commit();
            storageFragment = null;
        }

        if (soldItemEditor != null) {
            if (onUserLeaveHint == null) {
                getSupportFragmentManager().beginTransaction().
                        remove(soldItemEditor).
                        commit();
                soldItemEditor = null;
            }
        }*/

            super.onSaveInstanceState(outState);
        }

    public Bundle createBundle(@Nullable Bundle bundle, boolean isEditorUsed) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (storageFragment != null) {
            List<SoldItem> items = storageFragment.getItems();
            bundle.putParcelableArrayList("items", (ArrayList<SoldItem>) items);
            bundle.putInt("currentPosition", storageFragment.getListView().getFirstVisiblePosition());
        } else {
            bundle.putParcelableArrayList("items", (ArrayList<SoldItem>) testData);
        }

//        bundle.putBoolean("isCreated", isCreated);
//        bundle.putBoolean("isStorage", isStorage);
//        bundle.putBoolean("isClear", isClear);
//
//        if (soldItemEditor != null && !isEditorUsed) {
//            if ((isStorage == null || !isStorage) && onUserLeaveHint == null) {
//                currentItem = soldItemEditor.getCurrentItem();
//            }
//
//            if (currentItem != null && onUserLeaveHint == null) {
//                bundle.putParcelable("currentItem", currentItem);
//            }
//        }

        return bundle;
    }

    private SoldItem createItem(String title, SoldDestinationType type, double price, int amount) {
        SoldItem item = new SoldItem();
        item.setId(UUID.randomUUID());
        item.setTitle(title);
        item.setPrice(price);
        item.setAmount(amount);
        item.setType(type);

        return item;
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

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }
}
