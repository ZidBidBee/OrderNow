package com.example.android.ordernow.onclasses;

import java.util.ArrayList;

/**
 * Created by Ziyaad B on 2017/11/30.
 */

public class Order {
    public int OrderNumber;
    public ArrayList<ONItem> Items;
    public Double TotalPrice;

    public Order(){}

    public Order(int orderNumber, ArrayList<ONItem> kitchenItems, Double Total){
        OrderNumber = orderNumber;
        Items = kitchenItems;
        TotalPrice = Total;

    }
}
