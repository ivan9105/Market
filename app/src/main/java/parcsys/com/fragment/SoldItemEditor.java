package parcsys.com.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import parcsys.com.MainActivity;
import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;
import parcsys.com.marketfinal.R;

public class SoldItemEditor extends Fragment {
    private Spinner typeField;
    private EditText editTitle, editAmount, editPrice;

    public static final String OK = "OK", CANCEL = "CANCEL";

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

        initActions(view);

        return view;
    }

    private void initActions(View view) {
        Button okButton = (Button) view.findViewById(R.id.btn_ok);
        Button cancelButton = (Button) view.findViewById(R.id.btn_cancel);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra(OK, OK);
                    getActivity().startActivity(intent);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra(CANCEL, CANCEL);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    private boolean validateFields() {
        boolean res = true;
        StringBuilder sb = new StringBuilder();
        sb.append("Fields: ");

        if (editTitle.getText() == null || editTitle.getText().toString().equals("")) {
            sb.append("title, ");
            res = false;
        }

        if (editAmount.getText() == null || editAmount.getText().toString().equals("")) {
            sb.append("amount, ");
            res = false;
        }

        if (editPrice.getText() == null || editPrice.getText().toString().equals("")) {
            sb.append("price, ");
            res = false;
        }

        if (!res) {
            String message = sb.toString();
            message = message.substring(0, message.lastIndexOf(", "));
            message = String.format("%s %s", message, "can't be empty");
            createDialog(message);
            return false;
        }

        return true;
    }

    private void createDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message)
        .setCancelable(false).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        alert.getWindow().setAttributes(lp);
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
