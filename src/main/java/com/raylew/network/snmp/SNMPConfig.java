package com.raylew.network.snmp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * mib
 */
public class SNMPConfig {
    private Properties properties;

    public SNMPConfig() {
        properties = new Properties();
        String resourceName = "mib.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            properties.load(resourceStream);
        } catch (IOException e) {
            System.out.println("read properties error");
            e.printStackTrace();
        }
    }

    /**
     * get value by oid
     *
     * @param oid Object Identifier
     * @return
     */
    public String getValueByOID(String oid) {
        return properties.getProperty(oid);
    }

    /**
     * get next OID
     *
     * @param currentOID
     * @return
     */
    public String getNextOID(String currentOID) {
        String nextOID = "";
        Set<Object> objects = properties.keySet();
        Iterator<Object> iterator = objects.iterator();
        String firstOID = "";
        boolean found = false;
        boolean first = true;
        while (iterator.hasNext()) {
            String next = (String) iterator.next();
            if (first) {
                firstOID = next;
                first = false;
            }
            if (!found) {
                if (next.equals(currentOID)) {
                    found = true;
                }
            } else {
                nextOID = next;
            }
        }
        if (found && "".equals(nextOID)) {
            nextOID = firstOID;
        }
        return nextOID;
    }

    public void setValueByOID(String oid, String value) {
        properties.setProperty(oid, value);
        try {
            properties.store(new FileOutputStream("mib.properties"), "mib.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}  