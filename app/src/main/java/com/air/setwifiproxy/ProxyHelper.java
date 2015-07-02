package com.air.setwifiproxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Air on 15/7/2.
 */
public class ProxyHelper {
    //get ProxyProperties constructor
    public Object getProxySettings(String ip, int port) throws InstantiationException,
            IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
        Constructor proxyPropertiesCtor = getProxyPropertiesConstructor(getProxyPropertiesClass());
        //create the parameters for the constructor
        Object[] proxyPropertiesCtorParams = createConstructorParams(ip, port);

        //create a new object using the params
        return proxyPropertiesCtor.newInstance(proxyPropertiesCtorParams);
    }


    public Class getProxyPropertiesClass() throws ClassNotFoundException {
        //ProxyInfo.class
        return Class.forName(Constants.CONFIG_PROXY_PROPERTY);
    }

    public Object[] createConstructorParams(String ip, int port) {
        Object[] proxyPropertiesCtorParams = new Object[3];
        proxyPropertiesCtorParams[0] = ip;
        proxyPropertiesCtorParams[1] = port;
        proxyPropertiesCtorParams[2] = null;
        return proxyPropertiesCtorParams;
    }

    public Constructor getProxyPropertiesConstructor(Class proxyPropertiesClass) throws NoSuchMethodException {
        Class[] proxyPropertiesCtorParamTypes = new Class[3];
        proxyPropertiesCtorParamTypes[0] = String.class;
        proxyPropertiesCtorParamTypes[1] = int.class;
        proxyPropertiesCtorParamTypes[2] = String.class;

        return proxyPropertiesClass.getConstructor(proxyPropertiesCtorParamTypes);
    }

    public Class getConfigureClass() throws ClassNotFoundException, NoSuchMethodException {
        return Class.forName(Constants.CONFIG_CLASS_NAME);
    }
}
