package parcsys.com.fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import parcsys.com.adapters.SoldItemAdapter;
import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.marketfinal.R;

/**
 * Created by Иван on 25.01.2015.
 */
public class StorageFragment extends ListFragment {
    List<SoldItem> testData = new ArrayList<SoldItem>();
    private SoldItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int currentOrientation = getCurrentOrientation();
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            return inflater.inflate(R.layout.storage_potrait, null);
        } else {
            return inflater.inflate(R.layout.storage_landscape, null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addTestData();

        this.adapter = new SoldItemAdapter
                              (getActivity().getApplicationContext(), testData, getCurrentOrientation());
        setListAdapter(adapter);
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }

        /*
        Set<Integer> notEnabledSet = adapter.getNotEnabledSet();
        outState.putInt("length", notEnabledSet.toArray().length);
        for (int i = 0; i < notEnabledSet.toArray().length; i++) {
            outState.putInt(String.valueOf(i), (Integer) notEnabledSet.toArray()[i]);
        }*/
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        /*if (savedInstanceState != null && savedInstanceState.get("length") != null) {
            Integer length = (Integer) savedInstanceState.get("length");
            Set<Integer> notEnabledSet = new HashSet<Integer>();
            for (int i = 0; i < length; i++) {
                notEnabledSet.add((Integer) savedInstanceState.get(String.valueOf(i)));
            }
            adapter.setNotEnabledSet(notEnabledSet);
        }*/
        super.onViewStateRestored(savedInstanceState);
    }
}
