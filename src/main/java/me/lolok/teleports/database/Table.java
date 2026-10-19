package me.lolok.teleports.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Table {
    PREFERENCES("TeleportPreferences", "CREATE TABLE IF NOT EXISTS `TeleportPreferences` (`uuid` VARCHAR(36) NOT NULL, `requests` BIT NOT NULL DEFAULT 1, `auto_accept` BIT NOT NULL DEFAULT 0, `action_bar` BIT NOT NULL DEFAULT 1, PRIMARY KEY (uuid))"),
    TOGGLES("TeleportToggles", "CREATE TABLE IF NOT EXISTS `TeleportToggles` (`uuid` VARCHAR(36) NOT NULL, `target` VARCHAR(36) NOT NULL, FOREIGN KEY (uuid) REFERENCES Preferences(uuid))"),
    AUTO_ACCEPTS("TeleportAutoAccepts", "CREATE TABLE IF NOT EXISTS `TeleportAutoAccepts` (`uuid` VARCHAR(36) NOT NULL, `target` VARCHAR(36) NOT NULL, FOREIGN KEY (uuid) REFERENCES Preferences(uuid))");

    @Getter
    private final String name, query;

    protected static void initTables(ISQLManager manager) {
        Arrays.asList(Table.values()).forEach(table -> table.initTable(manager));
    }

    protected void initTable(ISQLManager manager) {
        manager.executeUpdate(getQuery());
    }
}
