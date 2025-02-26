package dev.al3mid3x.lib.events.menus;

import dev.al3mid3x.lib.events.LibEvent;
import dev.al3mid3x.lib.gui.LibMenu;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data public class MenuClickEvent extends LibEvent {
    private final Player player;
    private final LibMenu menu;
    private final int slot;
    private final ItemStack currentItem;

}
