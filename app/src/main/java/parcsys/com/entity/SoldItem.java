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
    private int amount;
    private double price;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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
        dest.writeStringArray(new String[]{id.toString(), title, type});
        dest.writeInt(amount);
        dest.writeDouble(price);
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
        this.id = UUID.fromString(data[0]);
        this.title = data[1];
        this.type = data[2];
        this.amount = parcel.readInt();
        this.price = parcel.readDouble();
    }
}
