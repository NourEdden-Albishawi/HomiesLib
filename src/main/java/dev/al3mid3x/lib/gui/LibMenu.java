package dev.al3mid3x.lib.gui;

import dev.al3mid3x.lib.utils.ItemFactory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class LibMenu {
    private final UUID uniqueId = UUID.randomUUID();

    private final Inventory inventory;

    private final String name;

    private Player playerWhoOpenedInventory;

    private int page = 0;


    protected abstract int getMaxItemsPerPage();

    protected abstract boolean isPaginated();

    public abstract List<LibItemMenu> getItems();

    public LibMenu(String name, int size) {
        this.name = name;
        this.inventory = Bukkit.createInventory(null, size, name);
    }

    public void consume(InventoryClickEvent event) {
        for (LibItemMenu item : getItems())
            item.getConsumer().accept(event, new ItemFactory(event.getCurrentItem()));
    }

    public void build() {
        if (isPaginated()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                int index = getMaxItemsPerPage() * this.page + i;
                if (index >= getItems().size())
                    break;
                for (LibItemMenu item : getItems())
                    this.inventory.setItem(item.getSlot(), item.getItem().complete());
            }
            return;
        }
        for (LibItemMenu item : getItems())
            this.inventory.setItem(item.getSlot(), item.getItem().complete());
    }

    public void openInventory(Player player) {
        player.openInventory(this.inventory);
    }

    public void Next() {
        if (getItems().size() >= getMaxItemsPerPage()) {
            this.page++;
            openInventory(getPlayerWhoOpenedInventory());
        }
    }

    public void Previous() {
        if (this.page != 0) {
            this.page--;
            openInventory(getPlayerWhoOpenedInventory());
        }
    }

}
