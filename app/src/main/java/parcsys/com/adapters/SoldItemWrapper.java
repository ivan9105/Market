package parcsys.com.adapters;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import parcsys.com.entity.SoldItem;
import parcsys.com.entity.enums.SoldDestinationType;

/**
 * Created by Иван on 21.04.2015.
 */
public class SoldItemWrapper implements Parcelable {
    private SoldItem soldItem;
    private boolean enable;

    public SoldItemWrapper(SoldItem soldItem, boolean enable) {
        this.enable = enable;
        this.soldItem = soldItem;
    }

    public SoldItem getSoldItem() {
        return soldItem;
    }

    public void setSoldItem(SoldItem soldItem) {
        this.soldItem = soldItem;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(soldItem.getId()),
                soldItem.getTitle(),
                soldItem.getType().getId()});
        if (soldItem.getAmount() != null) {
            dest.writeInt(soldItem.getAmount());
        } else {
            dest.writeInt(-1);
        }

        if (soldItem.getPrice() != null) {
            dest.writeDouble(soldItem.getPrice());
        } else {
            dest.writeDouble(-1);
        }

        dest.writeBooleanArray(new boolean[]{enable});
    }

    public static final Parcelable.Creator<SoldItemWrapper> CREATOR = new Parcelable.Creator<SoldItemWrapper>() {
        @Override
        public SoldItemWrapper createFromParcel(Parcel source) {
            return new SoldItemWrapper(source);
        }

        @Override
        public SoldItemWrapper[] newArray(int size) {
            return new SoldItemWrapper[0];
        }
    };

    private SoldItemWrapper(Parcel parcel) {
        String[] data = new String[3];
        parcel.readStringArray(data);

        this.soldItem = new SoldItem();

        if (data[0] != null) {
            this.soldItem.setId(UUID.fromString(data[0]));
        }
        this.soldItem.setTitle(data[1]);
        this.soldItem.setType(SoldDestinationType.getById(data[2]));

        int amountFromParcel = parcel.readInt();
        if (amountFromParcel == -1) {
            this.soldItem.setAmount(null);
        } else {
            this.soldItem.setAmount(amountFromParcel);
        }

        double priceFromParcel = parcel.readDouble();
        if (priceFromParcel == -1) {
            this.soldItem.setPrice(null);
        } else {
            this.soldItem.setPrice(priceFromParcel);
        }

        this.enable = parcel.readSparseBooleanArray().get(0);
    }
}
