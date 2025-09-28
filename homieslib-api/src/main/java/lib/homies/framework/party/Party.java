package lib.homies.framework.party;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Set;
import java.util.UUID;

/**
 * A platform-agnostic interface representing a party of players.
 * This interface defines the core contract for managing party members and communication.
 */
public interface Party {
    /**
     * Gets the unique ID of this party instance.
     * @return The UUID of the party.
     */
    UUID getId();

    /**
     * Gets the current leader of the party.
     * @return The {@link HomiesPlayer} who is the party leader.
     */
    HomiesPlayer getLeader();

    /**
     * Gets an unmodifiable set of all players currently in the party.
     * @return A {@link Set} of {@link HomiesPlayer}s who are members of this party.
     */
    Set<HomiesPlayer> getMembers();

    /**
     * Gets the name of the party.
     * @return The name of the party.
     */
    String getName();

    /**
     * Sends a message to all members of the party.
     * @param message The message string to send (color codes supported).
     */
    void sendMessage(String message);

    /**
     * Adds a player to the party.
     * This method should typically be called by the {@link PartyService} after an invitation is accepted.
     * @param player The {@link HomiesPlayer} to add to the party.
     */
    void addMember(HomiesPlayer player);

    /**
     * Removes a player from the party.
     * This method should typically be called by the {@link PartyService} when a player leaves or is kicked.
     * @param player The {@link HomiesPlayer} to remove from the party.
     */
    void removeMember(HomiesPlayer player);

    /**
     * Sets a new leader for the party.
     * This method should typically be called by the {@link PartyService} during leadership transfer or when the old leader leaves.
     * @param newLeader The {@link HomiesPlayer} to set as the new leader.
     */
    void setLeader(HomiesPlayer newLeader);

    /**
     * Checks if a player is a member of this party.
     * @param player The {@link HomiesPlayer} to check.
     * @return {@code true} if the player is a member, {@code false} otherwise.
     */
    boolean isMember(HomiesPlayer player);

    /**
     * Checks if a player is the leader of this party.
     * @param player The {@link HomiesPlayer} to check.
     * @return {@code true} if the player is the leader, {@code false} otherwise.
     */
    boolean isLeader(HomiesPlayer player);

    /**
     * Gets the current number of members in the party.
     * @return The size of the party.
     */
    int getSize();
}
