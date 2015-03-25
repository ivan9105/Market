package parcsys.com;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "tag";

    private FragmentTransaction fragmentTransaction;
    private StorageFragment storageFragment;
    private SoldItemEditor soldItemEditor;
    private List<SoldItem> testData = new ArrayList<SoldItem>();
    private Boolean isCreated;
    private Integer currentPosition;
    private Boolean isStorage;
    private Boolean isClear;
    private Boolean onUserLeaveHint;

    private SoldItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        restoreData(savedInstanceState);

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

        makeActionOverflowMenuShown();
    }

    private void checkIsStorage() {
        Intent intent = getIntent();
        if (!isClear && intent != null && (intent.getStringExtra(SoldItemEditor.OK) != null
                || intent.getStringExtra(SoldItemEditor.CANCEL) != null)) {
            restoreData(intent.getExtras());
            isStorage = true;
            currentItem = intent.getExtras().getParcelable("currentItem");
            if (currentItem != null) {
                //skip logic of adding - it simple
                testData.add(currentItem);
                intent.removeExtra("currentItem");
            }
            intent.removeExtra(SoldItemEditor.OK);
            intent.removeExtra(SoldItemEditor.CANCEL);
        }
    }

    private void createStorage() {
        storageFragment = new StorageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) testData);
        if (currentPosition != null) {
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
        isClear = false;
    }

    private void createEditor() {
        soldItemEditor = new SoldItemEditor();

        Bundle bundle = createBundle(null, true);
        bundle.putParcelable("currentItem", currentItem);

        soldItemEditor.setArguments(bundle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, soldItemEditor);
        fragmentTransaction.commit();

        isStorage = false;
    }

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState == null || isStorage != null && !isStorage) {
            Intent intent = getIntent();
            if (intent.getStringExtra(SoldItemEditor.OK) != null) {
                currentItem = intent.getParcelableExtra("currentItem");
                testData.add(currentItem);
            }
            return;
        }

        isStorage = (Boolean) savedInstanceState.get("isStorage");
        isCreated = (Boolean) savedInstanceState.get("isCreated");
        isClear = (Boolean) savedInstanceState.get("isClear");

        if (savedInstanceState.get("items") != null) {
            testData = savedInstanceState.getParcelableArrayList("items");
        }

        if (savedInstanceState.get("currentItem") != null) {
            currentItem = savedInstanceState.getParcelable("currentItem");
        }

        currentPosition = savedInstanceState.getInt("currentPosition");
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
                Toast.makeText(this, "Add item", Toast.LENGTH_SHORT)
                        .show();
                isClear = true;
                storageFragment = null;
                createEditor();
            default:
                break;
        }

        return true;
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

        createBundle(outState, false);

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
        }

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

        bundle.putBoolean("isCreated", isCreated);
        bundle.putBoolean("isStorage", isStorage);
        bundle.putBoolean("isClear", isClear);

        if (soldItemEditor != null && !isEditorUsed) {
            if ((isStorage == null || !isStorage) && onUserLeaveHint == null) {
                currentItem = soldItemEditor.getCurrentItem();
            }

            if (currentItem != null && onUserLeaveHint == null) {
                bundle.putParcelable("currentItem", currentItem);
            }
        }

        return bundle;
    }

    private void addTestData() {
        testData.add(createItem("IPhone6 Black 32Gb quarterCore 2048 MB, discount 5%, video 512 mb, raw 4 gb, size 520 x 650", SoldDestinationType.TECHNOLOGY, 60000.00, 4));
        testData.add(createItem("Huawey300Y Ascend doubleCore 1Gg 512 MB", SoldDestinationType.TECHNOLOGY, 5000.00, 5));
        testData.add(createItem("Sausiges Quality Factory 1 kg", SoldDestinationType.PRODUCTS, 245.00, 300));
        testData.add(createItem("Meat beaf 1 kg", SoldDestinationType.PRODUCTS, 400.00, 100));
        testData.add(createItem("Bread white 250 gr", SoldDestinationType.PRODUCTS, 25.00, 400));
        testData.add(createItem("Milk 1l", SoldDestinationType.PRODUCTS, 55.00, 300));
        testData.add(createItem("Eggs 40 items", SoldDestinationType.PRODUCTS, 65.00, 1300));
        testData.add(createItem("Huawey600Y Ascend quarterCore 3Gg 1024MB 17d", SoldDestinationType.TECHNOLOGY, 6245.00, 3));
        testData.add(createItem("IPhone4 White 16 Gb doubleCore 1024 Mb", SoldDestinationType.TECHNOLOGY, 12245.00, 30));
        testData.add(createItem("Shovel big great white", SoldDestinationType.HOUSEHOLD_GOODS, 1245.00, 130));
        testData.add(createItem("Brush", SoldDestinationType.HOUSEHOLD_GOODS, 145.00, 530));
        testData.add(createItem("Broom", SoldDestinationType.HOUSEHOLD_GOODS, 215.00, 330));
        testData.add(createItem("Scoop", SoldDestinationType.HOUSEHOLD_GOODS, 65.00, 330));
        testData.add(createItem("Towel", SoldDestinationType.HOUSEHOLD_GOODS, 165.00, 1330));
        testData.add(createItem("Chair", SoldDestinationType.HOUSEHOLD_GOODS, 10000, 7));
        testData.add(createItem("Red chair", SoldDestinationType.HOUSEHOLD_GOODS, 11000, 17));
        testData.add(createItem("Black chair", SoldDestinationType.HOUSEHOLD_GOODS, 11500, 11));
        testData.add(createItem("Mini sofa", SoldDestinationType.HOUSEHOLD_GOODS, 12000, 3));
        testData.add(createItem("Big sofa", SoldDestinationType.HOUSEHOLD_GOODS, 17000, 2));
        testData.add(createItem("Big white sofa", SoldDestinationType.HOUSEHOLD_GOODS, 19000, 4));
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
