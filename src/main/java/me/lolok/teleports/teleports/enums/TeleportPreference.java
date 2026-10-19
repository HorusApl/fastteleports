package me.lolok.teleports.teleports.enums;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.teleports.configurations.messages.Message;
import me.lolok.teleports.database.Table;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public enum TeleportPreference {
    TOGGLE_REQUESTS(true, "requests", Message.PREFERENCES_REQUESTS_TOGGLED, Message.PREFERENCES_REQUESTS_ADDED, Message.PREFERENCES_REQUESTS_REMOVED, Table.TOGGLES),
    AUTO_ACCEPT(false, "auto_accept", Message.PREFERENCES_AUTO_ACCEPT_TOGGLED, Message.PREFERENCES_AUTO_ACCEPT_ADDED, Message.PREFERENCES_AUTO_ACCEPT_REMOVED, Table.AUTO_ACCEPTS),
    ACTION_BAR(true, "action_bar", Message.PREFERENCES_ACTION_BAR_TOGGLED, null, null, null);

    @Getter
    private final boolean activeDefault;
    @Getter
    private final String field;
    @Getter
    private final Message toggled;
    @Getter @Nullable
    private final Message added, removed;
    private final Table table;

    public Table getTable() {
        Preconditions.checkState(table != null, "This preference doesn't have a support table.");
        return table;
    }

    public boolean hasTable() {
        return table != null;
    }
}
