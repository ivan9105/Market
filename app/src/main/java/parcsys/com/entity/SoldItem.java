package parcsys.com.entity;

import java.util.UUID;

import parcsys.com.entity.enums.SoldDestinationType;

/**
 * Created by Иван on 25.01.2015.
 */
public class SoldItem {
    private UUID id;
    private String title;
    private int amount;
    private double price;
    private String type;

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
}
