package parcsys.com.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.UUID;

import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.marketfinal.R;

public class SoldItemEditor extends Fragment {
    private Spinner typeField;
    private EditText editTitle, editAmount, editPrice;

    private SoldItem currentItem;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (getCurrentOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.sold_item_editor_portrait, null);
        } else {
            view = inflater.inflate(R.layout.sold_item_editor_landscape, null);
        }

        initFields(view);
        setItem();

        return view;
    }

    private void setItem() {
        currentItem = getArguments().getParcelable("currentItem");
        if (currentItem != null) {
            editTitle.setText(currentItem.getTitle());
            editAmount.setText(currentItem.getAmount() == null ? "" : String.valueOf(currentItem.getAmount()));
            editPrice.setText(currentItem.getPrice() == null ? "" : String.valueOf(currentItem.getPrice()));

            for (int i = 0; i < SoldDestinationType.values().length; i++) {
                if (SoldDestinationType.values()[i] == currentItem.getType()) {
                    typeField.setSelection(i);
                }
            }
        }
    }

    private void initFields(View view) {
        if (getCurrentOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            //may be create logic with set space position
            LinearLayout spaceLayout = (LinearLayout) view.findViewById(R.id.space);
            spaceLayout.setMinimumHeight(100);
        }

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeField.setAdapter(adapter);
    }

    public SoldItem getCurrentItem() {
        return getItem();
    }

    private SoldItem getItem() {
        if (currentItem == null) {
            SoldItem newItem = new SoldItem();
            newItem.setId(UUID.randomUUID());
            setValuesFromFields(newItem);
            return newItem;
        } else {
            setValuesFromFields(currentItem);
            return currentItem;
        }
    }

    private void setValuesFromFields(SoldItem newItem) {
        newItem.setTitle(editTitle.getText().toString());
        newItem.setAmount((editAmount.getText().toString().equals("") ? null : Integer.valueOf(editAmount.getText().toString())));
        newItem.setPrice((editPrice.getText().toString().equals("") ? null : Double.valueOf(editPrice.getText().toString())));

        for (SoldDestinationType destinationType : SoldDestinationType.values()) {
            if (destinationType.getId().equals(typeField.getSelectedItem())) {
                newItem.setType(destinationType);
            }
        }
    }

    private int getCurrentOrientation() {
        return getResources().getConfiguration().orientation;
    }
}
