package dev.al3mid3x.lib.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ItemFactory {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemFactory(ItemStack stack) {
        this.itemStack = stack;
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemFactory(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemFactory setDisplayName(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemFactory setLore(List<String> lore){
        this.itemMeta.setLore(lore);
        return this;
    }
    public ItemFactory addLore(String lore){
        this.itemMeta.getLore().add(lore);
        return this;
    }

    public ItemFactory setPlayerProfile(PlayerProfile profile) {
        if (itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setPlayerProfile(profile);
        }
        return this;
    }

    public ItemStack complete() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
