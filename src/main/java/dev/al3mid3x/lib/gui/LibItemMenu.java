package dev.al3mid3x.lib.gui;

import dev.al3mid3x.lib.utils.ItemFactory;
import lombok.Data;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;
import java.util.function.BiConsumer;

@Data public class LibItemMenu {
    private final UUID uuid;

    private final int slot;

    private final ItemFactory item;

    public final BiConsumer<InventoryClickEvent, ItemFactory> consumer;

}