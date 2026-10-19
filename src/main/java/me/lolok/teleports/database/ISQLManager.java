package me.lolok.teleports.database;

import me.lolok.teleports.Service;
import me.lolok.teleports.database.managers.SQLTeleportsRequestsManager;
import org.intellij.lang.annotations.Language;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

public interface ISQLManager extends Service {
    SQLTeleportsRequestsManager getTeleportsRequestsManager();

    void executeUpdate(@Language("SQL") String sql, Object... args);

    CompletableFuture<CachedRowSet> executeQuery(@Language("SQL") String sql, Object... args);

    Connection getConnection();

    Connection createConnection();

    void close();
}
