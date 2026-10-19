package me.lolok.teleports.database.driver;

import lombok.RequiredArgsConstructor;
import me.lolok.teleports.FastTeleportsPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RequiredArgsConstructor
public enum Driver {
    H2("H2", "com/h2database/h2/2.1.214/h2-2.1.214.jar"),
    SQLITE("SQLite", "org/xerial/sqlite-jdbc/3.41.2.1/sqlite-jdbc-3.41.2.1.jar");

    public final static Logger LOGGER = Logger.getLogger("FastTeleports-Driver");
    private final String name, repo;

    public File getFile() {
        File folder = new File(FastTeleportsPlugin.getInstance().getDataFolder().getAbsolutePath(), "libs");
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, name + ".jar");
    }

    public void download() {
        if (getFile().exists()) return;
        try (BufferedInputStream in = new BufferedInputStream(openConnection().getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(getFile())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            LOGGER.info("Successfully downloaded driver " + name);
        } catch (IOException e) {
            LOGGER.warning("Unable to download driver " + name);
            e.printStackTrace();
        }
    }

    private URL getURL() throws MalformedURLException {
        String link = String.format("https://nexus.lucko.me/repository/maven-central/%s", repo);
        return URI.create(link).toURL();
    }

    private URLConnection openConnection() throws IOException {
        URLConnection connection = getURL().openConnection();
        connection.setRequestProperty("User-Agent", "luckperms");
        connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5L));
        connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10L));
        return connection;
    }
}
