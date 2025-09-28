package lib.homies.framework.minigame;

import java.util.Optional;
import java.util.Set;

/**
 * A platform-agnostic interface for managing minigames within the framework.
 * This service allows for registering, unregistering, and retrieving {@link MiniGame} instances.
 */
public interface MiniGameManager {

    /**
     * Registers a new minigame with the manager.
     * The minigame will be accessible by its name.
     * @param game The {@link MiniGame} instance to register.
     */
    void registerGame(MiniGame game);

    /**
     * Unregisters a minigame from the manager.
     * @param name The name of the minigame to unregister.
     */
    void unregisterGame(String name);

    /**
     * Retrieves a registered minigame by its name.
     * @param name The name of the minigame.
     * @return An {@link Optional} containing the {@link MiniGame} if found, otherwise {@link Optional#empty()}.
     */
    Optional<MiniGame> getGame(String name);

    /**
     * Retrieves a set of all currently registered minigames.
     * @return An unmodifiable {@link Set} of all active {@link MiniGame} instances.
     */
    Set<MiniGame> getAllGames();
}
