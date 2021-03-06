package parcsys.com.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import parcsys.com.entity.enums.SoldDestinationType;

/**
 * Created by Иван on 25.01.2015.
 */
public class SoldItem implements Parcelable{
    private UUID id;
    private String title;
    private Integer amount;
    private Double price;
    private String type;

    public SoldItem(){}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public SoldDestinationType getType() {
        return SoldDestinationType.getById(type);
    }

    public void setType(SoldDestinationType type) {
        this.type = type.getId();
    }

    @Override
    public int describeContents() {
        //return default value
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(id),
                title,
                type});
        if (amount != null) {
            dest.writeInt(amount);
        } else {
            dest.writeInt(-1);
        }

        if (price != null) {
            dest.writeDouble(price);
        } else {
            dest.writeDouble(-1);
        }
    }

    public static final Creator<SoldItem> CREATOR = new Creator<SoldItem>() {
        @Override
        public SoldItem createFromParcel(Parcel source) {
            return new SoldItem(source);
        }

        @Override
        public SoldItem[] newArray(int size) {
            return new SoldItem[0];
        }
    };

    private SoldItem(Parcel parcel) {
        String[] data = new String[3];
        parcel.readStringArray(data);
        if (data[0] != null) {
            this.id = UUID.fromString(data[0]);
        }
        this.title = data[1];
        this.type = data[2];

        int amountFromParcel = parcel.readInt();
        if (amountFromParcel == -1) {
            this.amount = null;
        } else {
            this.amount = amountFromParcel;
        }

        double priceFromParcel = parcel.readDouble();
        if (priceFromParcel == -1) {
            this.price = null;
        } else {
            this.price = priceFromParcel;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SoldItem item = (SoldItem) o;

        if (!id.equals(item.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
