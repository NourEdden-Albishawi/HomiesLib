package lib.homies.framework.minigame;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * An abstract base class for creating {@link MiniGame} implementations.
 * This class provides common functionality for managing players, game state, and basic lifecycle methods.
 * Subclasses should implement the abstract methods to define specific game logic.
 */
public abstract class AbstractMiniGame implements MiniGame {

    private final String name;
    private final UUID id;
    private HomiesPlayer leader;
    private final Set<HomiesPlayer> members;
    private MiniGameState state = MiniGameState.STOPPED;

    /**
     * Constructs a new AbstractMiniGame.
     * @param name The unique name of the minigame.
     */
    public AbstractMiniGame(String name) {
        this.name = name;
        this.id = UUID.randomUUID(); // Generate a unique ID for each game instance
        this.members = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * Gets the unique ID of this minigame instance.
     * @return The UUID of the minigame.
     */
    @Override
    public final UUID getId() {
        return id;
    }

    /**
     * Gets the name of the minigame.
     * @return The name of the minigame.
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * Gets the current state of the minigame.
     * @return The current {@link MiniGameState}.
     */
    @Override
    public final MiniGameState getState() {
        return state;
    }

    /**
     * Sets the current state of the minigame.
     * @param newState The new {@link MiniGameState}.
     */
    protected final void setState(MiniGameState newState) {
        this.state = newState;
    }

    /**
     * Gets the leader of the minigame.
     * @return The {@link HomiesPlayer} who is the leader.
     */
    @Override
    public HomiesPlayer getLeader() {
        return leader;
    }

    /**
     * Sets the leader of the minigame.
     * @param newLeader The {@link HomiesPlayer} to set as the new leader.
     */
    protected final void setLeader(HomiesPlayer newLeader) {
        this.leader = newLeader;
    }

    /**
     * Gets an unmodifiable set of all players currently in the minigame.
     * @return A {@link Set} of {@link HomiesPlayer}s.
     */
    @Override
    public final Set<HomiesPlayer> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    /**
     * Starts the minigame.
     * Transitions the state to {@link MiniGameState#STARTING} then {@link MiniGameState#RUNNING}.
     * Calls the abstract {@link #onStart()} method for subclass-specific logic.
     */
    @Override
    public final void start() {
        if (this.state != MiniGameState.WAITING && this.state != MiniGameState.STOPPED) return;
        setState(MiniGameState.STARTING);
        onStart();
        setState(MiniGameState.RUNNING);
    }

    /**
     * Stops the minigame.
     * Transitions the state to {@link MiniGameState#ENDING} then {@link MiniGameState#STOPPED}.
     * Calls the abstract {@link #onStop()} method for subclass-specific logic.
     */
    @Override
    public final void stop() {
        if (this.state == MiniGameState.STOPPED) return;
        setState(MiniGameState.ENDING);
        onStop();
        setState(MiniGameState.STOPPED);
    }

    /**
     * Adds a player to the minigame.
     * Calls the abstract {@link #onPlayerJoin(HomiesPlayer)} method for subclass-specific logic.
     * @param player The {@link HomiesPlayer} to add.
     */
    @Override
    public final void addPlayer(HomiesPlayer player) {
        if (members.add(player)) {
            onPlayerJoin(player);
        }
    }

    /**
     * Removes a player from the minigame.
     * Calls the abstract {@link #onPlayerLeave(HomiesPlayer)} method for subclass-specific logic.
     * @param player The {@link HomiesPlayer} to remove.
     */
    @Override
    public final void removePlayer(HomiesPlayer player) {
        if (members.remove(player)) {
            onPlayerLeave(player);
        }
    }

    // --- Abstract methods for subclasses to implement ---

    /**
     * Called when the minigame is starting.
     * Subclasses should implement their game-specific startup logic here.
     */
    protected abstract void onStart();

    /**
     * Called when the minigame is stopping.
     * Subclasses should implement their game-specific cleanup logic here.
     */
    protected abstract void onStop();

    /**
     * Called when a player joins the minigame.
     * Subclasses can implement logic like teleporting the player, giving kits, etc.
     * @param player The player who joined.
     */
    protected abstract void onPlayerJoin(HomiesPlayer player);

    /**
     * Called when a player leaves the minigame.
     * Subclasses can implement logic like removing the player from the game area, resetting their state, etc.
     * @param player The player who left.
     */
    protected abstract void onPlayerLeave(HomiesPlayer player);
}
