package me.lolok.teleports.database.driver.drivers;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.database.driver.Driver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.Properties;

public class SQLiteDriver extends AbstractConnectionDriver {
    public SQLiteDriver() {
        super("org.sqlite.jdbc4.JDBC4Connection", Driver.SQLITE);
    }

    @Override
    public Constructor<?> getConstructor() throws ClassNotFoundException, NoSuchMethodException, MalformedURLException {
        return getConstructor(String.class, String.class, Properties.class);
    }

    @Override
    public Connection getConnection() {
        return getConnection("jdbc:sqlite:" + getDatabaseFile().getPath(), getDatabaseFile().getPath(), new Properties());
    }

    public File getDatabaseFile() {
        File folder = new File(FastTeleportsPlugin.getInstance().getDataFolder(), "data");
        if (!folder.exists()) folder.mkdirs();
        File file = new File(folder, "database.db");
        try {
            if (file.createNewFile())
                Driver.LOGGER.info("Successfully create SQLite database!");
        } catch (IOException e) {
            Driver.LOGGER.warning("Unable to create SQLite database!");
            e.printStackTrace();
        }
        return file;
    }
}
