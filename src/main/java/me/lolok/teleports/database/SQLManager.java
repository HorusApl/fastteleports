package me.lolok.teleports.database;

import lombok.Getter;
import me.lolok.teleports.FastTeleportsPlugin;
import me.lolok.teleports.configurations.files.DatabaseConfiguration;
import me.lolok.teleports.database.driver.ConnectionDriver;
import me.lolok.teleports.database.managers.SQLTeleportsRequestsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Language;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class SQLManager implements ISQLManager {
    public final static Logger LOGGER = Logger.getLogger("FastTeleports-SQLManager");
    private final JavaPlugin plugin;
    @Getter private Connection connection;
    @Getter private final SQLTeleportsRequestsManager teleportsRequestsManager = new SQLTeleportsRequestsManager(this);

    public SQLManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        this.connection = createConnection();
        Table.initTables(this);
    }

    @Override
    public void disable() {
        close();
    }

    @Override
    public void executeUpdate(@Language("SQL") String sql, Object... args) {
        if (connection == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (args.length != 0)
                    for (int i = 1; i <= args.length; i++)
                        statement.setObject(i, args[i - 1]);

                statement.executeUpdate();
            } catch (SQLException e) {
                LOGGER.warning("Unable to execute the query " + sql);
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<CachedRowSet> executeQuery(String sql, Object... args) {
        return CompletableFuture.supplyAsync(() -> {
            CachedRowSet set;
            try (PreparedStatement statement = FastTeleportsPlugin.getInstance().getSqlManager().getConnection().prepareStatement(sql)) {
                if (args != null && args.length != 0)
                    for (int i = 1; i <= args.length; i++)
                        statement.setObject(i, args[i - 1]);

                set = RowSetProvider.newFactory().createCachedRowSet();
                set.populate(statement.executeQuery());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return set;
        }, (cmd) -> Bukkit.getScheduler().runTaskAsynchronously(plugin, cmd));
    }

    @Override
    public Connection createConnection() {
        String driverName = FastTeleportsPlugin.getInstance().getConfigurationService().getConfiguration("database", DatabaseConfiguration.class).getDriver().toUpperCase();
        try {
            ConnectionDriver driver = ConnectionDriver.valueOf(driverName);
            return driver.getDriver().getConnection();
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Unknown driver: " + driverName);
        }
        return null;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}