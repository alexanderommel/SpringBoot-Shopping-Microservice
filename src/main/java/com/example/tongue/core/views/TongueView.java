package com.example.tongue.core.views;

public class TongueView {

    //Customers view
    public static interface CustomerView{
    }
    public static interface MerchantView extends CustomerView{
    }
    public static interface AdminView extends MerchantView{
    }
}
