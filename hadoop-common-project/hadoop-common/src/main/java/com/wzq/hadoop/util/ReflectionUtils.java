package com.wzq.hadoop.util;

import com.wzq.hadoop.conf.Configurable;
import com.wzq.hadoop.conf.Configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * General reflection utils
 */
public class ReflectionUtils {

    private static final Class<?>[] EMPTY_ARRAY = new Class[]{};

    /**
     * Cache of constructors for each class. Pins the classes so they can't be garbage
     * collected until ReflectionUtils can be collected.
     */
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE =
            new ConcurrentHashMap<Class<?>, Constructor<?>>();

    /**
     * Check and set configuration if necessary
     * @param theObject object for which to set configuration
     * @param conf {@link java.util.Collection}
     */
    public static void setConf(Object theObject, Configuration conf) {
        if (conf != null) {
            if (theObject instanceof Configurable) {
                ((Configurable) theObject).setConf(conf);
            }
            setJobConf(theObject, conf);
        }
    }

    private static void setJobConf(Object theObject,Configuration conf) {
        // TODO
    }

    public static <T> T newInstance(Class<T> theClass, Configuration conf) {
        T result;
        try {
            Constructor<T> meth = (Constructor<T>) CONSTRUCTOR_CACHE.get(theClass);
            if (meth == null) {
                meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
                meth.setAccessible(true);
                CONSTRUCTOR_CACHE.put(theClass, meth);
            }
            result = meth.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setConf(result, conf);
        return result;
    }

}
