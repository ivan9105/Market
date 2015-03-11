package parcsys.com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.marketfinal.R;

public class SoldItemEditor extends Fragment {
    private Spinner typeField;
    private EditText editTitle, editAmount, editPrice;
    private ArrayAdapter<String> adapter;

    private final static String LOG_TAG = "logTag";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.sold_item_editor, null);
        initFields(view);
        setItem(savedInstanceState);

        return view;
    }

    private void setItem(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        SoldItem item = savedInstanceState.getParcelable("item");
        editTitle.setText(item.getTitle());
        editAmount.setText(item.getAmount() == null ? "" : String.valueOf(item.getAmount()));
        editPrice.setText(item.getPrice() == null ? "" : String.valueOf(item.getPrice()));

        for (int i = 0; i < SoldDestinationType.values().length; i++) {
            if (SoldDestinationType.values()[i] == item.getType()) {
                typeField.setSelection(i);
            }
        }
    }

    private void initFields(View view) {
        editTitle = (EditText) view.findViewById(R.id.editTitle);
        editAmount = (EditText) view.findViewById(R.id.editAmount);
        editPrice = (EditText) view.findViewById(R.id.editPrice);

        initSpinner(view);
    }

    private void initSpinner(View view) {
        typeField = (Spinner) view.findViewById(R.id.soldType);
        SoldDestinationType[] values = SoldDestinationType.values();
        final ArrayList<String> data = new ArrayList<String>();
        for (SoldDestinationType destinationType : values) {
            data.add(destinationType.getId());
        }

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeField.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState");
        outState.putParcelable("item", getItem());
        super.onSaveInstanceState(outState);
    }

    private SoldItem getItem() {
        SoldItem item = new SoldItem();
        item.setId(UUID.randomUUID());
        item.setTitle(editTitle.getText().toString());
        item.setAmount((editAmount.getText().toString().equals("") ? null : Integer.valueOf(editAmount.getText().toString())));
        item.setPrice((editPrice.getText().toString().equals("") ? null : Double.valueOf(editPrice.getText().toString())));

        for (SoldDestinationType destinationType : SoldDestinationType.values()) {
            if (destinationType.getId().equals(typeField.getSelectedItem())) {
                item.setType(destinationType);
            }
        }

        return item;
    }
}
