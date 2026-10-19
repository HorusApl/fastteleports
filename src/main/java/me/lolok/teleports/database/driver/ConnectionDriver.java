package me.lolok.teleports.database.driver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.teleports.database.driver.drivers.AbstractConnectionDriver;
import me.lolok.teleports.database.driver.drivers.H2Driver;
import me.lolok.teleports.database.driver.drivers.SQLiteDriver;

@RequiredArgsConstructor
public enum ConnectionDriver {
    H2(new H2Driver()),
    SQLITE(new SQLiteDriver());

    @Getter
    private final AbstractConnectionDriver driver;
}
