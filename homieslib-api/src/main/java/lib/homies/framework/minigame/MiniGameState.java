package lib.homies.framework.minigame;

/**
 * Represents the possible states a {@link MiniGame} can be in.
 */
public enum MiniGameState {
    /**
     * The minigame is waiting for players or configuration.
     */
    WAITING,
    /**
     * The minigame is in the process of starting (e.g., countdown).
     */
    STARTING,
    /**
     * The minigame is actively running.
     */
    RUNNING,
    /**
     * The minigame is in the process of ending (e.g., showing results).
     */
    ENDING,
    /**
     * The minigame has finished and is no longer active.
     */
    STOPPED
}
