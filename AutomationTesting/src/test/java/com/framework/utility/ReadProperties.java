package com.framework.utility;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReadProperties {

    private static Properties prop = new Properties();

    @Nullable
    public static Properties getProperties() {
        FileInputStream fileInput;
        File file = new File("src/test/resources/config/config.properties");
        try {
            fileInput = new FileInputStream(file);
        } catch (IOException ex) {
            System.out.println(ex.toString());
            return null;
        }
        try {
            prop.load(fileInput);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
