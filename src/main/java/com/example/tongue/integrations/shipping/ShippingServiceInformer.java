package com.example.tongue.integrations.shipping;

public class ShippingServiceInformer {

    private static String port="8081";
    private static String host="localhost";
    private static String authority= "https://" +host+":"+port+"/";

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        ShippingServiceInformer.port = port;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        ShippingServiceInformer.host = host;
    }

    public static String getAuthority() {
        return authority;
    }

    public static void setAuthority(String authority) {
        ShippingServiceInformer.authority = authority;
    }

}
