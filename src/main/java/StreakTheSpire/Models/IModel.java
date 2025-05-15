package StreakTheSpire.Models;

import java.util.UUID;

public class IModel {
    private transient UUID uuid = UUID.randomUUID();
    public UUID getUUID() {
        return uuid;
    }
}
