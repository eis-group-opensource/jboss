package org.jboss.serial.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

public class LegacyHashMapReader {

    public static ThreadLocal<Boolean> ENABLED = ThreadLocal.withInitial(() -> false);

    private static Method reinitializeMethod;

    private static Field mapField;

    static {
        try {
            reinitializeMethod = HashMap.class.getDeclaredMethod("reinitialize", new Class[0]);
            reinitializeMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readObject(HashMap map, java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        // Read in the threshold (ignored), loadfactor, and any hidden stuff
        stream.defaultReadObject();

        stream.readInt();                // Read and ignore number of buckets
        int mappings = stream.readInt(); // Read number of mappings (size)
        if (mappings < 0)
            throw new InvalidObjectException("Illegal mappings count: " +
                    mappings);
        else if (mappings > 0) {
            try {
                //Call reinitialize because some cunt code (possibly stream.defaultReadObject()) is messing around
                // with threshold value of the HashMap between creation and population
                reinitializeMethod.invoke(map, null);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < mappings; i++) {
                Object key = stream.readObject();
                Object value = stream.readObject();
                map.put(key, value);
            }
        }
    }

    private static final Object PRESENT = new Object();

    public static void readObject(HashSet set, java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {

        stream.defaultReadObject();

        // Read capacity and verify non-negative.
        int capacity = stream.readInt();
        if (capacity < 0) {
            throw new InvalidObjectException("Illegal capacity: " +
                    capacity);
        }

        // Read load factor and verify positive and non NaN.
        float loadFactor = stream.readFloat();
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new InvalidObjectException("Illegal load factor: " +
                    loadFactor);
        }

        // Read size and verify non-negative.
        int size = stream.readInt();
        if (size < 0) {
            throw new InvalidObjectException("Illegal size: " +
                    size);
        }

        HashMap map = new HashMap();
        try {
            mapField.set(set, map);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            Object e = stream.readObject();
            map.put(e, PRESENT);
        }

    }

}