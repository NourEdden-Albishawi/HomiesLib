package dev.al3mid3x.lib.managers;

import com.google.common.collect.Maps;
import dev.al3mid3x.lib.gui.LibMenu;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuManager {
    private final Plugin plugin;

    private final Map<UUID, LibMenu> menuMap = Maps.newHashMap();

    public MenuManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public LibMenu get(UUID uuid) {
        if (this.menuMap.containsKey(uuid)) return this.menuMap.get(uuid);
        plugin.getLogger().severe("Couldn't find LibMenu object with id of: " + uuid.toString());
        return null;
    }

    public List<LibMenu> getAll() {
        return this.menuMap.values().stream().toList();
    }

    public LibMenu findByName(String name) {
        return this.menuMap.values().stream().filter(v -> v.getName().equalsIgnoreCase(name)).findFirst().get();
    }

    public void insert(LibMenu menu) {
        if (!this.menuMap.containsKey(menu.getUniqueId())) {
            this.menuMap.put(menu.getUniqueId(), menu);
        }
    }

    public void update(LibMenu menu) {
        delete(menu);
        insert(menu);
    }

    public void delete(LibMenu menu) {
        this.menuMap.remove(menu.getUniqueId());
    }
}
