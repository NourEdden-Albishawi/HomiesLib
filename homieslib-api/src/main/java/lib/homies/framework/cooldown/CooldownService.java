package lib.homies.framework.cooldown;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A platform-agnostic service for managing time-based cooldowns for players.
 * Framework developers can use this to implement delays for abilities, commands, etc.
 */
public interface CooldownService {

    /**
     * Checks if a specific key is currently on cooldown for a given player.
     *
     * @param player The {@link HomiesPlayer} to check.
     * @param key    A unique string identifier for the cooldown (e.g., "teleport_command", "special_ability").
     * @return {@code true} if the key is still on cooldown for the player, {@code false} otherwise.
     */
    boolean isOnCooldown(HomiesPlayer player, String key);

    /**
     * Puts a specific key on cooldown for a player for a given duration.
     * If the player already has a cooldown for this key, it will be reset to the new duration.
     *
     * @param player   The {@link HomiesPlayer} to apply the cooldown to.
     * @param key      A unique string identifier for the cooldown.
     * @param duration The length of the cooldown.
     * @param unit     The {@link TimeUnit} for the duration (e.g., TimeUnit.SECONDS, TimeUnit.MINUTES).
     */
    void setCooldown(HomiesPlayer player, String key, long duration, TimeUnit unit);

    /**
     * Gets the remaining cooldown time for a specific key for a player.
     *
     * @param player The {@link HomiesPlayer} to check.
     * @param key    A unique string identifier for the cooldown.
     * @param unit   The {@link TimeUnit} in which the remaining time should be returned.
     * @return An {@link Optional} containing the remaining duration in the specified time unit if the key is on cooldown,
     *         or {@link Optional#empty()} if not on cooldown or if the cooldown has expired.
     */
    Optional<Long> getRemainingCooldown(HomiesPlayer player, String key, TimeUnit unit);

    /**
     * Removes an active cooldown for a specific key for a player.
     * If no such cooldown exists, this method does nothing.
     *
     * @param player The {@link HomiesPlayer} whose cooldown should be removed.
     * @param key    The unique string identifier for the cooldown to remove.
     */
    void removeCooldown(HomiesPlayer player, String key);
}
