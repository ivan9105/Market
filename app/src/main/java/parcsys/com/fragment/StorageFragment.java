package parcsys.com.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import parcsys.com.MainActivity;
import parcsys.com.adapters.SoldItemAdapter;
import parcsys.com.adapters.SoldItemWrapper;
import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.marketfinal.R;

/**
 * Created by Иван on 25.01.2015.
 */
public class StorageFragment extends ListFragment {
    public static final String NAME = "storage";

    private static MainActivity mainActivity;

    public StorageFragment() {
    }

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
        List<SoldItemWrapper> testData = getArguments().getParcelableArrayList("items");
        SoldItemAdapter adapter = new SoldItemAdapter
                (getActivity().getApplicationContext(), testData, getCurrentOrientation());
        setListAdapter(adapter);

        int currentPosition = getArguments().getInt("currentPosition");
        getListView().setSelection(currentPosition);
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }

    public void setActivity(MainActivity mainActivity) {
        StorageFragment.mainActivity = mainActivity;
    }

    public static void createBuyTask(SoldItem item) {
        mainActivity.createDisableBuyAction(item);
    }
}
