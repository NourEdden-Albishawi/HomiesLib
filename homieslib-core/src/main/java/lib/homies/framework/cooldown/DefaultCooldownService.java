package lib.homies.framework.cooldown;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Default, platform-independent implementation of the {@link CooldownService}.
 * This service manages cooldowns in-memory, associating them with player UUIDs and unique keys.
 */
public class DefaultCooldownService implements CooldownService {

    // Map: Player UUID (String) -> Map: Cooldown Key (String) -> Expiration Time (milliseconds, Long)
    private final Map<String, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    /**
     * Checks if a specific key is currently on cooldown for a given player.
     *
     * @param player The {@link HomiesPlayer} to check.
     * @param key    A unique string identifier for the cooldown (e.g., "teleport_command", "special_ability").
     * @return {@code true} if the key is still on cooldown for the player, {@code false} otherwise.
     */
    @Override
    public boolean isOnCooldown(HomiesPlayer player, String key) {
        return getRemainingCooldown(player, key, TimeUnit.MILLISECONDS).isPresent();
    }

    /**
     * Puts a specific key on cooldown for a player for a given duration.
     * If the player already has a cooldown for this key, it will be reset to the new duration.
     *
     * @param player   The {@link HomiesPlayer} to apply the cooldown to.
     * @param key      A unique string identifier for the cooldown.
     * @param duration The duration of the cooldown.
     * @param unit     The {@link TimeUnit} for the duration.
     */
    @Override
    public void setCooldown(HomiesPlayer player, String key, long duration, TimeUnit unit) {
        long expirationTime = System.currentTimeMillis() + unit.toMillis(duration);
        cooldowns.computeIfAbsent(player.getUniqueId().toString(), k -> new ConcurrentHashMap<>()).put(key, expirationTime);
    }

    /**
     * Gets the remaining cooldown time for a specific key for a player.
     *
     * @param player The {@link HomiesPlayer} to check.
     * @param key    A unique string identifier for the cooldown.
     * @param unit   The {@link TimeUnit} in which the remaining time should be returned.
     * @return An {@link Optional} containing the remaining duration in the specified time unit if the key is on cooldown,
     *         or {@link Optional#empty()} if not on cooldown or if the cooldown has expired.
     */
    @Override
    public Optional<Long> getRemainingCooldown(HomiesPlayer player, String key, TimeUnit unit) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId().toString());
        if (playerCooldowns == null) {
            return Optional.empty();
        }

        Long expirationTime = playerCooldowns.get(key);
        if (expirationTime == null) {
            return Optional.empty();
        }

        long remainingMillis = expirationTime - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            playerCooldowns.remove(key); // Cooldown expired, clean up
            return Optional.empty();
        }

        return Optional.of(unit.convert(remainingMillis, TimeUnit.MILLISECONDS));
    }

    /**
     * Removes a cooldown for a specific key for a player.
     * If no such cooldown exists, this method does nothing.
     *
     * @param player The {@link HomiesPlayer} whose cooldown should be removed.
     * @param key    The unique key for the cooldown to remove.
     */
    @Override
    public void removeCooldown(HomiesPlayer player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId().toString());
        if (playerCooldowns != null) {
            playerCooldowns.remove(key);
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(player.getUniqueId().toString()); // Clean up player entry if no more cooldowns
            }
        }
    }
}
