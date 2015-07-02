package com.air.setwifiproxy;

import android.os.Build;

/**
 * Created by Air on 15/7/2.
 */
public class Constants {
    public final static String CONFIG_PROPERTY;
    public final static String CONFIG_CLASS_NAME;
    public final static String CONFIG_PROXY_PROPERTY;

    static {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CONFIG_PROPERTY = "mIpConfiguration";
            CONFIG_CLASS_NAME = "android.net.IpConfiguration";
            CONFIG_PROXY_PROPERTY = "android.net.ProxyInfo";
        }else {
            CONFIG_PROPERTY = "linkProperties";
            CONFIG_CLASS_NAME = "android.net.LinkProperties";
            CONFIG_PROXY_PROPERTY = "android.net.ProxyProperties";
        }
    }
}
