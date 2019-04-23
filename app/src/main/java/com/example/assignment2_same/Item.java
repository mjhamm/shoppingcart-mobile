package com.example.assignment2_same;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Item implements Comparable<Item>{

    private String product;
    private Integer priority;
    private Integer quantity;
    private String price;
    private String purchased;
    private String notPurchased;

    @Override
    public String toString() {
        return
                getProduct() + "\t\tx" + getQuantity() + "\n\n" + "Total price for items = $" + getPrice() + "\t\t\t\tPriority = " + getPriority();
    }

    public Item(String product, Integer priority, Integer quantity, String price, String purchased, String notPurchased) {
        this.product = new String(product);
        this.priority = new Integer(priority);
        this.quantity = new Integer(quantity);
        this.price = new String(price);
        this.purchased = new String(purchased);
        this.notPurchased = new String(notPurchased);
    }

    public void setProduct(String productname) {
        this.product = productname;
    }

    public String getProduct() {
        return product;
    }

    public Integer getPriority() {
        return priority;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getPurchased() {
        return purchased;
    }

    public String getNotPurchased() {
        return notPurchased;
    }

    @Override
    public int compareTo(Item item) {
        return (this.getPriority() < item.getPriority() ? -1 :
                (this.getPriority() == item.getPriority() ? 0 : 1));
    }
}