package parcsys.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    public SoldItemAdapter(Context ctx, List<SoldItem> items) {
        super(ctx, R.layout.sold_item, items);
        this.ctx = ctx;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sold_item, parent, false);
        SoldItem item = items.get(position);

        ((TextView) view.findViewById(R.id.soldTitleField)).setText(item.getTitle());
        ((TextView) view.findViewById(R.id.soldPriceField)).setText(doubleFormat(item.getPrice()));
        ((TextView) view.findViewById(R.id.soldAmountField)).setText(String.valueOf(item.getAmount()));

        return view;
    }

    private String doubleFormat(double price) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(price);
    }
}
