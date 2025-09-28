package lib.homies.framework.minigame;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Set;
import java.util.UUID;

/**
 * A platform-agnostic interface representing a minigame.
 * This interface defines the core contract for any minigame within the framework.
 */
public interface MiniGame {
    /**
     * Gets the unique ID of this minigame instance.
     * @return The UUID of the minigame.
     */
    UUID getId();

    /**
     * Gets the leader of the minigame.
     * @return The {@link HomiesPlayer} who is the leader.
     */
    HomiesPlayer getLeader();

    /**
     * Gets an unmodifiable set of all players currently in the minigame.
     * @return A {@link Set} of {@link HomiesPlayer}s.
     */
    Set<HomiesPlayer> getMembers();

    /**
     * Gets the name of the minigame.
     * @return The name of the minigame.
     */
    String getName();

    /**
     * Starts the minigame.
     * This method should transition the game state and initiate game logic.
     */
    void start();

    /**
     * Stops the minigame.
     * This method should transition the game state and clean up game resources.
     */
    void stop();

    /**
     * Adds a player to the minigame.
     * @param player The {@link HomiesPlayer} to add.
     */
    void addPlayer(HomiesPlayer player);

    /**
     * Removes a player from the minigame.
     * @param player The {@link HomiesPlayer} to remove.
     */
    void removePlayer(HomiesPlayer player);

    /**
     * Gets the current state of the minigame.
     * @return The current {@link MiniGameState}.
     */
    MiniGameState getState();
}
