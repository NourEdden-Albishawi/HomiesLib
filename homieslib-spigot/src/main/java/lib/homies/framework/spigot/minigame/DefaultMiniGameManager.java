package lib.homies.framework.spigot.minigame;

import lib.homies.framework.minigame.MiniGame;
import lib.homies.framework.minigame.MiniGameManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A default, platform-agnostic implementation of the {@link MiniGameManager}.
 * This class manages the registration and retrieval of {@link MiniGame} instances
 * within the framework.
 */
public class DefaultMiniGameManager implements MiniGameManager {

    private final Map<String, MiniGame> activeGames = new ConcurrentHashMap<>();

    /**
     * Registers a new minigame with the manager.
     * The minigame will be accessible by its name (case-insensitive).
     * If a game with the same name is already registered, it will be overwritten.
     * @param game The {@link MiniGame} instance to register.
     */
    @Override
    public void registerGame(MiniGame game) {
        activeGames.put(game.getName().toLowerCase(), game);
    }

    /**
     * Unregisters a minigame from the manager.
     * @param name The name of the minigame to unregister (case-insensitive).
     */
    @Override
    public void unregisterGame(String name) {
        activeGames.remove(name.toLowerCase());
    }

    /**
     * Retrieves a registered minigame by its name.
     * @param name The name of the minigame (case-insensitive).
     * @return An {@link Optional} containing the {@link MiniGame} if found, otherwise {@link Optional#empty()}.
     */
    @Override
    public Optional<MiniGame> getGame(String name) {
        return Optional.ofNullable(activeGames.get(name.toLowerCase()));
    }

    /**
     * Retrieves a set of all currently registered minigames.
     * @return An unmodifiable {@link Set} of all active {@link MiniGame} instances.
     */
    @Override
    public Set<MiniGame> getAllGames() {
        return Collections.unmodifiableSet(new HashSet<>(activeGames.values()));
    }
}
