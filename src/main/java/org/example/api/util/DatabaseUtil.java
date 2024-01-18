package org.example.api.util;

import org.example.api.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    public static String getConnectionUrl() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                logger.error("Sorry, unable to find application.properties");
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
            logger.error("Sorry, unable to set connection to database %s%n"+ex);
            return null;
        }
    }

    public static String getUser() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                logger.error("Sorry, unable to find application.properties");
                return null;
            }

            prop.load(input);

            return prop.getProperty("postgres.db.username");
        } catch (IOException ex) {
            logger.error("Sorry, unable to get user for database %s%n"+ex);
            return null;
        }
    }

    public static String getPassword() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                logger.error("Sorry, unable to find application.properties");
                return null;
            }

            prop.load(input);

            return prop.getProperty("postgres.db.password");
        } catch (IOException ex) {
            logger.error("Sorry, unable to get password for database %s%n"+ex);
            return null;
        }
    }
}
