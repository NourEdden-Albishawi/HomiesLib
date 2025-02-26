package dev.al3mid3x.lib.events.menus;

import dev.al3mid3x.lib.events.LibEvent;
import dev.al3mid3x.lib.gui.LibMenu;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class MenuCloseEvent extends LibEvent {
    private final Player player;
    private final LibMenu menu;

}
