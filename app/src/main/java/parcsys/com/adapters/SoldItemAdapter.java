package parcsys.com.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import parcsys.com.entity.SoldItem;
import parcsys.com.fragment.StorageFragment;
import parcsys.com.marketfinal.R;
import parcsys.com.utils.DaoStaticUtils;
import parcsys.com.utils.FormatterHelper;

/**
 * Created by Иван on 25.01.2015.
 */
public class SoldItemAdapter extends ArrayAdapter<SoldItemWrapper> {
    private Context ctx;
    private List<SoldItemWrapper> items;
    private int currentOrientation;

    public SoldItemAdapter(Context ctx, List<SoldItemWrapper> items, int currentOrientation) {
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


        SoldItemWrapper wrapper = items.get(position);
        SoldItem item = wrapper.getSoldItem();

        String titleText = item.getTitle();
        if (titleText.length() > 64) {
            titleText = titleText.substring(0, 60) + "...";
        }
        ((TextView) view.findViewById(R.id.soldTitleField)).setText(titleText);
        ((TextView) view.findViewById(R.id.soldPriceField)).setText(FormatterHelper.doubleFormat(item.getPrice()));
        ((TextView) view.findViewById(R.id.soldAmountField)).setText(String.valueOf(item.getAmount()));
        ((TextView) view.findViewById(R.id.typeField)).setText("Type: " + item.getType().getId());

        Button buttonBuy = (Button) view.findViewById(R.id.btn_buy);
        buttonBuy.setOnClickListener(new BuyOnClickListener(position, view));
        if (!wrapper.isEnable()) {
            buttonBuy.setEnabled(false);
        }

        return view;
    }

    private class BuyOnClickListener implements View.OnClickListener {
        private int position;
        private View parentView;

        public BuyOnClickListener(int position, View parentView) {
            this.position = position;
            this.parentView = parentView;
        }

        @Override
        public void onClick(View v) {
            SoldItemWrapper wrapper = items.get(position);
            SoldItem item = wrapper.getSoldItem();
            if (item.getAmount() - 1 > 0) {
                item.setAmount(item.getAmount() - 1);
                DaoStaticUtils.getDao().updateItem(item);
                wrapper.setEnable(false);
                StorageFragment.disableItem(item.getId());
            } else {
                items.remove(position);
                DaoStaticUtils.getDao().removeItem(item);
            }
            notifyDataSetChanged();
        }
    }
}
