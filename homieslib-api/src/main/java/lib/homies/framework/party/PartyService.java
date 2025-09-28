package lib.homies.framework.party;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A platform-agnostic service for managing parties within the framework.
 * This service provides methods for creating, disbanding, joining, and leaving parties,
 * as well as handling invitations and leadership transfers.
 */
public interface PartyService {

    /**
     * Creates a new party with the given player as its leader.
     * @param leader The {@link HomiesPlayer} who will be the leader of the new party.
     * @return The newly created {@link Party} instance, or {@code null} if the leader is already in a party.
     */
    Party createParty(HomiesPlayer leader);

    /**
     * Disbands an existing party.
     * All members will be removed from the party.
     * @param party The {@link Party} instance to disband.
     */
    void disbandParty(Party party);

    /**
     * Gets a party by its unique ID.
     * @param partyId The {@link UUID} of the party.
     * @return An {@link Optional} containing the {@link Party} if found, otherwise {@link Optional#empty()}.
     */
    Optional<Party> getParty(UUID partyId);

    /**
     * Gets the party a player is currently a member of.
     * @param player The {@link HomiesPlayer} to check.
     * @return An {@link Optional} containing the {@link Party} if the player is in one, otherwise {@link Optional#empty()}.
     */
    Optional<Party> getPartyByPlayer(HomiesPlayer player);

    /**
     * Gets an unmodifiable set of all currently active parties.
     * @return A {@link Set} of all active {@link Party} instances.
     */
    Set<Party> getAllParties();

    /**
     * Invites a player to a party.
     * The {@code inviter} must be the leader of a party to send an invitation.
     * Various checks are performed (inviter in party, inviter is leader, invited not in party, no pending invitation).
     * @param inviter The {@link HomiesPlayer} sending the invitation (must be a party leader).
     * @param invited The {@link HomiesPlayer} to invite to the party.
     */
    void invitePlayer(HomiesPlayer inviter, HomiesPlayer invited);

    /**
     * Accepts a party invitation.
     * The {@code player} will join the party of the {@code inviter} if a valid invitation exists.
     * @param player The {@link HomiesPlayer} accepting the invitation.
     * @param inviter The {@link HomiesPlayer} who sent the invitation.
     */
    void acceptInvitation(HomiesPlayer player, HomiesPlayer inviter);

    /**
     * Declines a party invitation.
     * @param player The {@link HomiesPlayer} declining the invitation.
     * @param inviter The {@link HomiesPlayer} who sent the invitation.
     */
    void declineInvitation(HomiesPlayer player, HomiesPlayer inviter);

    /**
     * Kicks a member from a party.
     * The {@code leader} must be the leader of the party to perform a kick.
     * @param leader The {@link HomiesPlayer} who is the party leader performing the kick.
     * @param member The {@link HomiesPlayer} to kick from the party.
     */
    void kickMember(HomiesPlayer leader, HomiesPlayer member);

    /**
     * Allows a player to leave their current party.
     * If the player is the leader and other members exist, leadership will be transferred.
     * If the party becomes empty, it will be disbanded.
     * @param player The {@link HomiesPlayer} leaving the party.
     */
    void leaveParty(HomiesPlayer player);

    /**
     * Transfers leadership of a party from the current leader to another member.
     * The {@code currentLeader} must be the actual leader, and {@code newLeader} must be a member of the party.
     * @param currentLeader The {@link HomiesPlayer} who is the current leader.
     * @param newLeader The {@link HomiesPlayer} to transfer leadership to (must be a current member).
     */
    void transferLeadership(HomiesPlayer currentLeader, HomiesPlayer newLeader);
}
