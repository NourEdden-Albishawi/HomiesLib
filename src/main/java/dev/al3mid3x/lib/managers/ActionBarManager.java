package dev.al3mid3x.lib.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ActionBarManager {
    private final boolean isPaper;
    private final Plugin plugin;

    public ActionBarManager(Plugin plugin) {
        this.plugin = plugin;
        this.isPaper = Bukkit.getServer().getClass().getPackage().getName().contains("paper");
    }

    public void send(Player player, String message) {
        if (isPaper) {
            Component component = LegacyComponentSerializer.legacySection().deserialize(message);
            player.sendActionBar(component);
        } else {
            sendActionBarLegacy(player, message);
        }
    }

    public void sendAll(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, message);
        }
    }

    public void sendTimed(Player player, String message, int durationTicks) {
        send(player, message);
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> send(player, ""), durationTicks);
    }

    private void sendActionBarLegacy(Player player, String message) {
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> chatComponentClass = Class.forName("net.minecraft.server." + getServerVersion() + ".IChatBaseComponent");

            Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + getServerVersion() + ".IChatBaseComponent$ChatSerializer");
            Method chatSerializerMethod = chatSerializerClass.getMethod("a", String.class);

            String jsonMessage = "{\"text\": \"" + message + "\"}";
            Object chatComponent = chatSerializerMethod.invoke(null, jsonMessage);

            Class<?> packetClass = Class.forName("net.minecraft.server." + getServerVersion() + ".PacketPlayOutChat");
            Constructor<?> packetConstructor = packetClass.getConstructor(chatComponentClass, byte.class);

            Object packet = packetConstructor.newInstance(chatComponent, (byte) 2);

            Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
            Object entityPlayer = getHandleMethod.invoke(craftPlayer);
            Method sendPacketMethod = entityPlayer.getClass().getField("playerConnection").getType().getMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(entityPlayer.getClass().getField("playerConnection").get(entityPlayer), packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
