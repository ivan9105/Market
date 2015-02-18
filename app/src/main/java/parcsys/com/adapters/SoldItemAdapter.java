package parcsys.com.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = inflater.inflate(R.layout.sold_item_landscape, parent, false);
        } else {
            view = inflater.inflate(R.layout.sold_item_portrait, parent, false);
        }


        SoldItem item = items.get(position);

        String titleText = item.getTitle();
        if (titleText.length() > 80) {
            titleText = titleText.substring(0, 78) + "...";
        }
        ((TextView) view.findViewById(R.id.soldTitleField)).setText(titleText);
        ((TextView) view.findViewById(R.id.soldPriceField)).setText(doubleFormat(item.getPrice()));
        ((TextView) view.findViewById(R.id.soldAmountField)).setText(String.valueOf(item.getAmount()));

        return view;
    }

    private String doubleFormat(double price) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(price);
    }
}
