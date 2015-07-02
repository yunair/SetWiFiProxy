package com.air.setwifiproxy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.air.setwifiproxy.ReflectionHelper.getDeclaredField;
import static com.air.setwifiproxy.ReflectionHelper.setEnumField;


public class MainActivity extends Activity implements View.OnClickListener {
    private final static String IP_STR = "IP";
    private final static String PORT_STR = "PORT";
    private WifiManager mWifiManager;
    private WifiConfiguration config;

    private Button mSetButton;
    private Button mUnsetButton;
    private EditText mIPText;
    private EditText mPortText;

    private SharedPreferences mSharedPreferences;
    private ProxyHelper mProxyHelper;



//    public enum ProxySettings {
        /* No proxy is to be used. Any existing proxy settings
         * should be cleared. */
//        NONE,
        /* Use statically configured proxy. Configuration can be accessed
         * with linkProperties */
//        STATIC,
        /* no proxy details are assigned, this is used to indicate
         * that any existing proxy settings should be retained */
//        UNASSIGNED
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        config = getWifiConfiguration();
        mProxyHelper = new ProxyHelper();
        initView();
    }

    private void saveIPAndPortToSF() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(IP_STR, getIP());
        editor.putInt(PORT_STR, getPort());
        editor.apply();
    }

    private String getIP() {
        return mIPText.getText().toString().trim();
    }

    private int getPort() {
        return Integer.parseInt(mPortText.getText().toString());
    }

    private void setIPAndPortToEdit() {
        mIPText.setText(mSharedPreferences.getString(IP_STR, ""));
        //setText不能用int值
        mPortText.setText(mSharedPreferences.getInt(PORT_STR, R.integer.port) + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setIPAndPortToEdit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveIPAndPortToSF();
    }

    private void initView() {
        mSharedPreferences = getSharedPreferences("wifiProxy", MODE_PRIVATE);
        mSetButton = (Button) findViewById(R.id.set_button);
        mUnsetButton = (Button) findViewById(R.id.unset_button);

        mIPText = (EditText) findViewById(R.id.ip_text);
        mPortText = (EditText) findViewById(R.id.port_text);

        mSetButton.setOnClickListener(this);
        mUnsetButton.setOnClickListener(this);
    }

    private WifiConfiguration getWifiConfiguration() {
        if (!mWifiManager.isWifiEnabled())
            return null;

        WifiConfiguration wifiConfiguration = null;
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        List<WifiConfiguration> wifiConfigurations = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConf : wifiConfigurations) {
            if (wifiConf.networkId == wifiInfo.getNetworkId()) {
                wifiConfiguration = wifiConf;
                break;
            }
        }
        return wifiConfiguration;
    }

    private void setWifiProxy(final String ip, final int port) {
        try {
            setProxyWithParams(mProxyHelper.getProxySettings(ip, port));
            //save the settings
            saveWifiChangeSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsetWifiProxy() {
        try {
            setProxyWithParams(null);
            //save the config
            saveWifiChangeSettings();
        } catch (Exception e) {
        }
    }

    private void setProxyWithParams(Object param) throws
            ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchFieldException, NoSuchMethodException, InstantiationException {

        //get the setHttpProxy method for LinkProperties
        Class configClass = mProxyHelper.getConfigureClass();
        //get the link properties from the wifi configuration
        Object configProperties = getDeclaredField(config, Constants.CONFIG_PROPERTY);
        if (null == configProperties)
            return;

//        Class[] setHttpProxyParams = new Class[1];
//        setHttpProxyParams[0] = mProxyHelper.getProxyPropertiesClass();
        Method setHttpProxy = configClass.getDeclaredMethod("setHttpProxy", mProxyHelper.getProxyPropertiesClass());
        setHttpProxy.setAccessible(true);
        //pass null as the proxy will clear proxy setting
//        Object[] params = new Object[1];
//        params[0] = param;
        setHttpProxy.invoke(configProperties, param);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Method getHttpProxySettings = configClass.getDeclaredMethod("getProxySettings");
            getHttpProxySettings.setAccessible(true);
            Method setProxy = config.getClass().getDeclaredMethod(
                    "setProxy",
                    getHttpProxySettings.invoke(configProperties).getClass(),
                    ProxyInfo.class);
            setProxy.setAccessible(true);
            setProxySettings(configProperties, param);
            setProxy.invoke(config,
                    getHttpProxySettings.invoke(configProperties), param);
        }else {
            setProxySettings(config, param);
        }
    }

    private void setProxySettings(Object configPro, Object param) throws NoSuchFieldException,
            IllegalAccessException {
        final String assign = ((param == null) ? "NONE" : "STATIC");
        setEnumField(configPro, "proxySettings", assign);
    }



    private void saveWifiChangeSettings() {
        mWifiManager.updateNetwork(config);
        mWifiManager.disconnect();
        mWifiManager.reconnect();
    }

    @Override
    public void onClick(View v) {
        if (null == config)
            return;
        final int id = v.getId();
        switch (id) {
            case R.id.set_button:
                setWifiProxy(getIP(), getPort());
                saveIPAndPortToSF();
                break;
            case R.id.unset_button:
                unsetWifiProxy();
                break;
        }
    }


}
