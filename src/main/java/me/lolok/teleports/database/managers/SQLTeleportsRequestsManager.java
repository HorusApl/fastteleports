package me.lolok.teleports.database.managers;

import lombok.RequiredArgsConstructor;
import me.lolok.teleports.database.ISQLManager;
import me.lolok.teleports.teleports.enums.TeleportPreference;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class SQLTeleportsRequestsManager {
    private final ISQLManager manager;

    public void setRequestsPreference(TeleportPreference preference, UUID uuid, boolean value) {
        manager.executeQuery(String.format("SELECT `%s` FROM TeleportPreferences WHERE `uuid` = ?", preference.getField()), uuid.toString()).whenComplete(((set, throwable) -> {
            if (throwable != null)
                return;
            try {
                if (!set.next())
                    manager.executeUpdate(String.format("INSERT INTO TeleportPreferences(uuid, %s) VALUES (?, ?)", preference.getField()), uuid.toString(), value ? 1 : 0);
                else
                    manager.executeUpdate(String.format("UPDATE TeleportPreferences SET `%s` = ? WHERE `uuid` = ?", preference.getField()), value ? 1 : 0, uuid.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public CompletableFuture<Boolean> getRequestsPreference(TeleportPreference preference, UUID uuid) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        manager.executeQuery(String.format("SELECT `%s` FROM TeleportPreferences WHERE `uuid` = ?", preference.getField()), uuid.toString()).whenComplete(((preferences, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                future.complete(false);
                return;
            }
            try {
                if (preferences.next()) {
                    boolean active = preferences.getInt(preference.getField()) == 1;
                    future.complete(active);
                } else
                    future.complete(preference.isActiveDefault());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        return future;
    }

    public CompletableFuture<List<UUID>> getPreferenceTargetUUIDs(TeleportPreference preference, UUID uuid) {
        CompletableFuture<List<UUID>> future = new CompletableFuture<>();
        manager.executeQuery(String.format("SELECT `target` FROM %s WHERE `uuid` = ?", preference.getTable().getName()), uuid.toString()).whenComplete((set, throwable) -> {
            if (throwable != null) {
                future.complete(null);
            } else
                try {
                    List<UUID> uuids = new ArrayList<>();
                    while (set.next()) {
                        UUID entry = UUID.fromString(set.getString("target"));
                        uuids.add(entry);
                    }
                    future.complete(uuids);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        });
        return future;
    }

    public CompletableFuture<Boolean> hasTeleportPreferenceTarget(TeleportPreference preference, UUID uuid, UUID target) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        manager.executeQuery(String.format("SELECT `target` FROM %s WHERE `uuid` = ? AND `target` = ?", preference.getTable().getName()), uuid.toString(), target.toString()).whenComplete((toggles, ignored) -> {
            try {
                future.complete(toggles.next());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return future;
    }

    public void addTeleportPreferenceTarget(TeleportPreference preference, UUID uuid, UUID target) {
        manager.executeQuery(String.format("SELECT `%s` FROM TeleportPreferences WHERE `uuid` = ?", preference.getField()), uuid.toString()).whenComplete(((set, throwable) -> {
            if (throwable != null)
                return;
            try {
                if (!set.next())
                    manager.executeUpdate(String.format("INSERT INTO TeleportPreferences(uuid, %s) VALUES (?, ?)", preference.getField()), uuid.toString(), 0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        manager.executeQuery(String.format("SELECT `target` FROM %s WHERE `uuid` = ? AND `target` = ?", preference.getTable().getName()), uuid.toString(), target.toString()).whenComplete(((set, throwable) -> {
            if (throwable != null)
                return;
            try {
                if (!set.next())
                    manager.executeUpdate(String.format("INSERT INTO %s (uuid, target) VALUES (?, ?)", preference.getTable().getName()), uuid.toString(), target.toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void removeTeleportPreferenceTarget(TeleportPreference preference, UUID uuid, UUID target) {
        manager.executeUpdate(String.format("DELETE FROM %s WHERE `uuid` = ? AND `target` = ?", preference.getTable().getName()), uuid.toString(), target.toString());
    }
}
