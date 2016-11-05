package com.raylew.network.snmp;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * mib
 */
public class SNMPConfig {
    private Properties properties;
    private Map map;

    public SNMPConfig() {
        properties = new Properties();
        String resourceName = "mib.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            properties.load(resourceStream);
        } catch (IOException e) {
            System.out.println("读取properties文件错误");
            e.printStackTrace();
        }
    }

    /**
     * 根据oid获取value
     *
     * @param oid
     * @return
     */
    public String getValueByOID(String oid) {
        return properties.getProperty(oid);

    }

    public void setValueByOID(String oid, String value) {
        properties.setProperty(oid, value);
        try {
            properties.store(new FileOutputStream("mib.properties"), "mib.properties");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    //测试主函数
    public static void main(String[] args) {
        SNMPConfig cfg = new SNMPConfig();
        String oid = "1.3.6.1.2.1.1.8.0";
        System.out.println("---------" + cfg.getValueByOID(oid));
        cfg.setValueByOID(oid, "test");
        System.out.println("---------" + cfg.getValueByOID(oid));
    }
}  