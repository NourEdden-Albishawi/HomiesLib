package dev.al3mid3x.lib.entities;

import java.util.UUID;

public abstract class LibMenu {
    private final UUID uuid;
    private final String name;
    private final int size;

    public abstract List<LibItemMenu> getItems();

    protected LibMenu(UUID uuid, String name, int size) {
        this.uuid = uuid;
        this.name = name;
        this.size = size;
    }
}
