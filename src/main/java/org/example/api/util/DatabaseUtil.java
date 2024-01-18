package org.example.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseUtil {
    public static String getConnectionUrl() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return null;
            }

            prop.load(input);

            return "jdbc:postgresql://" +
                    prop.getProperty("postgres.db.host") +
                    ":" +
                    prop.getProperty("postgres.db.port") +
                    "/" +
                    prop.getProperty("postgres.db.database") +
                    "?currentSchema=public";
        } catch (IOException ex) {
            System.out.println("Sorry, unable to set connection to database %s%n"+ex.getMessage());
            return null;
        }
    }

    public static String getUser() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return null;
            }

            prop.load(input);

            return prop.getProperty("postgres.db.username");
        } catch (IOException ex) {
            System.out.println("Sorry, unable to get user for database %s%n"+ex.getMessage());
            return null;
        }
    }

    public static String getPassword() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return null;
            }

            prop.load(input);

            return prop.getProperty("postgres.db.password");
        } catch (IOException ex) {
            System.out.println("Sorry, unable to get password for database %s%n"+ex.getMessage());
            return null;
        }
    }
}
