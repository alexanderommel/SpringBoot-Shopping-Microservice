package com.example.tongue.integration.shipping;

public class ShippingServiceInformer {

    public static String port="8081";
    public static String host="localhost";
    public static String authority= "https://" +host+":"+port+"/";
    public static String shippingRequestUrl="https://shipping-service/shipping/request_shipping";
    public static String shippingSummaryUrl="https://shipping-service/shipping/summary";

}
