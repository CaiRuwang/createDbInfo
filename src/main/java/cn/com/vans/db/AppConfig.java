package cn.com.vans.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Appconfig class
 *
 * @author vans
 * @date 2019/09/23
 */
public class AppConfig {

    public static Properties getProperties(String fileName) {
        try {
            String outpath = System.getProperty("user.dir")+ File.separator;
            System.out.println(outpath);
            Properties properties = new Properties();
            InputStream in = new FileInputStream(new File(outpath + fileName));
            properties.load(in);
            return properties;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            try {
                Properties properties = new Properties();
                InputStream in = CreateExcel.class.getClassLoader().getResourceAsStream(fileName);
                properties.load(in);
                return properties;
            } catch (IOException es) {
                System.out.println(es.getMessage());
                return null;
            }
        }
    }





}
