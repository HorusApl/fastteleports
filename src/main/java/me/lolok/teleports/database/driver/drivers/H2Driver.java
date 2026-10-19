package me.lolok.teleports.database.driver.drivers;

import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.database.driver.Driver;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.Properties;

public class H2Driver extends AbstractConnectionDriver {
    public H2Driver() {
        super("org.h2.jdbc.JdbcConnection", Driver.H2);
    }

    @Override
    public Constructor<?> getConstructor() throws ClassNotFoundException, NoSuchMethodException, MalformedURLException {
        return getConstructor(String.class, Properties.class);
    }

    @Override
    public Connection getConnection() {
        return getConnection("jdbc:h2:" + getDatabaseFile().getPath(), new Properties());
    }

    public File getDatabaseFile() {
        File folder = new File(FastTeleportsPlugin.getInstance().getDataFolder(), "data");
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, "database");
    }
}
