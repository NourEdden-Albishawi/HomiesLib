package lib.homies.framework.party;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Default, platform-independent implementation of the {@link Party} interface.
 * This class manages party members, leader, and provides basic party functionality.
 * It leverages {@link lib.homies.framework.utils.MessageUtils} for sending messages to party members.
 */
public class DefaultParty implements Party {

    private final UUID id;
    private HomiesPlayer leader;
    private final Set<HomiesPlayer> members;
    private final String name; // Optional: could be dynamic or set by leader

    /**
     * Constructs a new DefaultParty with the given player as the initial leader.
     * A unique ID is generated for the party, and the leader is automatically added as a member.
     * @param leader The {@link HomiesPlayer} who will be the leader of the new party.
     */
    public DefaultParty(HomiesPlayer leader) {
        this.id = UUID.randomUUID();
        this.leader = leader;
        this.members = Collections.synchronizedSet(new HashSet<>());
        this.members.add(leader);
        this.name = leader.getName() + "'s Party"; // Default party name
    }

    /**
     * Gets the unique ID of this party instance.
     * @return The UUID of the party.
     */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Gets the current leader of the party.
     * @return The {@link HomiesPlayer} who is the party leader.
     */
    @Override
    public HomiesPlayer getLeader() {
        return leader;
    }

    /**
     * Gets an unmodifiable set of all players currently in the party.
     * @return A {@link Set} of {@link HomiesPlayer}s who are members of this party.
     */
    @Override
    public Set<HomiesPlayer> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    /**
     * Gets the name of the party.
     * @return The name of the party.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sends a message to all members of the party.
     * @param message The message string to send (color codes supported).
     */
    @Override
    public void sendMessage(String message) {
        members.forEach(member -> HomiesLib.getMessageUtils().send(member, "&b[Party] &f" + message));
    }

    /**
     * Adds a player to the party.
     * If the player is successfully added, a message is sent to all party members.
     * @param player The {@link HomiesPlayer} to add to the party.
     */
    @Override
    public void addMember(HomiesPlayer player) {
        if (members.add(player)) {
            sendMessage(player.getName() + " has joined the party.");
        }
    }

    /**
     * Removes a player from the party.
     * If the player is successfully removed, a message is sent to all party members.
     * If the removed player was the leader, leadership is transferred to another member.
     * If the party becomes empty, it is implicitly handled by the {@link PartyService}.
     * @param player The {@link HomiesPlayer} to remove from the party.
     */
    @Override
    public void removeMember(HomiesPlayer player) {
        if (members.remove(player)) {
            sendMessage(player.getName() + " has left the party.");
            if (members.isEmpty()) {
                // Party is empty, should be disbanded by PartyService
            } else if (player.equals(leader)) {
                // Leader left, transfer leadership to the first available member
                setLeader(members.iterator().next());
            }
        }
    }

    /**
     * Sets a new leader for the party.
     * A message is sent to all party members announcing the leadership change.
     * @param newLeader The {@link HomiesPlayer} to set as the new leader.
     */
    @Override
    public void setLeader(HomiesPlayer newLeader) {
        if (members.contains(newLeader)) {
            HomiesPlayer oldLeader = this.leader;
            this.leader = newLeader;
            sendMessage(oldLeader.getName() + " has transferred leadership to " + newLeader.getName() + ".");
        }
    }

    /**
     * Checks if a player is a member of this party.
     * @param player The {@link HomiesPlayer} to check.
     * @return {@code true} if the player is a member, {@code false} otherwise.
     */
    @Override
    public boolean isMember(HomiesPlayer player) {
        return members.contains(player);
    }

    /**
     * Checks if a player is the leader of this party.
     * @param player The {@link HomiesPlayer} to check.
     * @return {@code true} if the player is the leader, {@code false} otherwise.
     */
    @Override
    public boolean isLeader(HomiesPlayer player) {
        return leader.equals(player);
    }

    /**
     * Gets the current number of members in the party.
     * @return The size of the party.
     */
    @Override
    public int getSize() {
        return members.size();
    }
}
