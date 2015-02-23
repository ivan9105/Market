package parcsys.com.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parcsys.com.entity.SoldItem;
import parcsys.com.marketfinal.R;

/**
 * Created by Иван on 25.01.2015.
 */
public class SoldItemAdapter extends ArrayAdapter<SoldItem> {
    private Context ctx;
    private List<SoldItem> items;
    private int currentOrientation;

    public SoldItemAdapter(Context ctx, List<SoldItem> items, int currentOrientation) {
        super(ctx, R.layout.sold_item_portrait, items);
        this.ctx = ctx;
        this.items = items;
        this.currentOrientation = currentOrientation;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = inflater.inflate(R.layout.sold_item_landscape, parent, false);
        } else {
            view = inflater.inflate(R.layout.sold_item_portrait, parent, false);
        }


        SoldItem item = items.get(position);

        String titleText = item.getTitle();
        if (titleText.length() > 64) {
            titleText = titleText.substring(0, 60) + "...";
        }
        ((TextView) view.findViewById(R.id.soldTitleField)).setText(titleText);
        ((TextView) view.findViewById(R.id.soldPriceField)).setText(doubleFormat(item.getPrice()));
        ((TextView) view.findViewById(R.id.soldAmountField)).setText(String.valueOf(item.getAmount()));
        ((TextView) view.findViewById(R.id.typeField)).setText("Type: " + item.getType().getId());

        Button buttonBuy = (Button) view.findViewById(R.id.btn_buy);
        buttonBuy.setOnClickListener(new BuyOnClickListener(position));

        return view;
    }

    private String doubleFormat(double price) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(price);
    }

    public List<SoldItem> getItems() {
        return items;
    }

    public void setItems(List<SoldItem> items) {
        this.items = items;
    }

    private class BuyOnClickListener implements View.OnClickListener {
        private int position;

        public BuyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            SoldItem soldItem = items.get(position);
            soldItem.setAmount(soldItem.getAmount() - 1);
            notifyDataSetChanged();
        }
    }
}
