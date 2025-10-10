package lib.homies.framework.spigot.world;

import lib.homies.framework.world.HomiesWorld;
import org.bukkit.World;

public class SpigotWorld implements HomiesWorld {

    private final World world;

    public SpigotWorld(World world) {
        this.world = world;
    }

    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public <T> T getAs(Class<T> platformClass) {
        if (platformClass.isInstance(world)) {
            return platformClass.cast(world);
        }
        return null;
    }

    public World getWorld() {
        return world;
    }
}
