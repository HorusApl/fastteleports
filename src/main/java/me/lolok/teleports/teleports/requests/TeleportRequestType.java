package me.lolok.teleports.teleports.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.teleports.configurations.messages.Message;

@RequiredArgsConstructor
public enum TeleportRequestType {
    TO(Message.TELEPORT_RECEIVED_TO),
    HERE(Message.TELEPORT_RECEIVED_HERE);

    @Getter
    private final Message message;
}
