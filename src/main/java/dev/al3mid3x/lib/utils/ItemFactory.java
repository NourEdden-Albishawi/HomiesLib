package dev.al3mid3x.lib.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public ItemStack complete() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }}
