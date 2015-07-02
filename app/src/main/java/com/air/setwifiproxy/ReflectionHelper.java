package com.air.setwifiproxy;

import java.lang.reflect.Field;

/**
 * Created by Air on 15/7/1.
 */
public class ReflectionHelper {
    public static Object getField(Object obj, String name)
            throws NoSuchFieldException, IllegalAccessException{
        //get class field
        Field f = obj.getClass().getField(name);
        //get field value
        return f.get(obj);
    }

    public static Object getDeclaredField(Object object, String name) throws NoSuchFieldException, IllegalAccessException {
        Field f = object.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(object);

    }

    public static void setEnumField(Object obj, String name, String value) throws
            NoSuchFieldException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }
}
