package me.lolok.teleports.database.driver.drivers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.teleports.database.driver.Driver;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;

@RequiredArgsConstructor
@Getter
public abstract class AbstractConnectionDriver {
    private final String className;
    private final Driver driver;

    public Connection getConnection(Object... objects) {
        try {
            return (Connection) getConstructor().newInstance(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected Class<?> getJDBCClass() throws MalformedURLException, ClassNotFoundException {
        URLClassLoader classLoader = new URLClassLoader(
                new URL[] {driver.getFile().toURI().toURL()},
                this.getClass().getClassLoader()
        );

        return Class.forName(className, true, classLoader);
    }

    protected Constructor<?> getConstructor(Class<?>... types) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException {
        return getJDBCClass().getConstructor(types);
    }

    public abstract Connection getConnection();

    protected abstract Constructor<?> getConstructor() throws ClassNotFoundException, NoSuchMethodException, MalformedURLException;

    protected abstract File getDatabaseFile();
}
