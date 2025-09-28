package lib.homies.framework.party;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default, platform-independent implementation of the {@link PartyService} interface.
 * This service manages the lifecycle of parties, including creation, disbanding, invitations,
 * and member interactions. It leverages other framework services like {@link lib.homies.framework.utils.MessageUtils}
 * for player feedback.
 */
public class DefaultPartyService implements PartyService {

    // Map: Party ID (UUID) -> Party instance
    private final Map<UUID, Party> activeParties = new ConcurrentHashMap<>();
    // Map: Invited Player UUID (UUID) -> Party ID (UUID) of the party that invited them
    private final Map<UUID, UUID> pendingInvitations = new ConcurrentHashMap<>();

    /**
     * Creates a new party with the given player as its leader.
     * If the leader is already in a party, an error message is sent.
     * @param leader The {@link HomiesPlayer} who will be the leader of the new party.
     * @return The newly created {@link Party} instance, or {@code null} if the leader is already in a party.
     */
    @Override
    public Party createParty(HomiesPlayer leader) {
        if (getPartyByPlayer(leader).isPresent()) {
            HomiesLib.getMessageUtils().send(leader, "&cYou are already in a party!");
            return null;
        }
        Party party = new DefaultParty(leader);
        activeParties.put(party.getId(), party);
        HomiesLib.getMessageUtils().send(leader, "&aYou have created a new party!");
        return party;
    }

    /**
     * Disbands an existing party.
     * All members will be removed from the party, and any pending invitations for its members will be cleared.
     * @param party The {@link Party} instance to disband.
     */
    @Override
    public void disbandParty(Party party) {
        activeParties.remove(party.getId());
        party.sendMessage("&cYour party has been disbanded!");
        // Remove any pending invitations for members of this party
        party.getMembers().forEach(member -> pendingInvitations.remove(member.getUniqueId()));
    }

    /**
     * Gets a party by its unique ID.
     * @param partyId The {@link UUID} of the party.
     * @return An {@link Optional} containing the {@link Party} if found, otherwise {@link Optional#empty()}.
     */
    @Override
    public Optional<Party> getParty(UUID partyId) {
        return Optional.ofNullable(activeParties.get(partyId));
    }

    /**
     * Gets the party a player is currently a member of.
     * @param player The {@link HomiesPlayer} to check.
     * @return An {@link Optional} containing the {@link Party} if the player is in one, otherwise {@link Optional#empty()}.
     */
    @Override
    public Optional<Party> getPartyByPlayer(HomiesPlayer player) {
        return activeParties.values().stream()
                .filter(party -> party.isMember(player))
                .findFirst();
    }

    /**
     * Gets an unmodifiable set of all currently active parties.
     * @return A {@link Set} of all active {@link Party} instances.
     */
    @Override
    public Set<Party> getAllParties() {
        return Collections.unmodifiableSet(new HashSet<>(activeParties.values()));
    }

    /**
     * Invites a player to a party.
     * The {@code inviter} must be the leader of a party to send an invitation.
     * Various checks are performed (inviter in party, inviter is leader, invited not in party, no pending invitation).
     * @param inviter The {@link HomiesPlayer} sending the invitation (must be a party leader).
     * @param invited The {@link HomiesPlayer} to invite to the party.
     */
    @Override
    public void invitePlayer(HomiesPlayer inviter, HomiesPlayer invited) {
        Optional<Party> inviterPartyOpt = getPartyByPlayer(inviter);
        if (inviterPartyOpt.isEmpty()) {
            HomiesLib.getMessageUtils().send(inviter, "&cYou are not in a party.");
            return;
        }
        Party inviterParty = inviterPartyOpt.get();

        if (!inviterParty.isLeader(inviter)) {
            HomiesLib.getMessageUtils().send(inviter, "&cOnly the party leader can invite players.");
            return;
        }

        if (getPartyByPlayer(invited).isPresent()) {
            HomiesLib.getMessageUtils().send(inviter, "&c" + invited.getName() + " is already in a party.");
            return;
        }

        if (pendingInvitations.containsKey(invited.getUniqueId())) {
            HomiesLib.getMessageUtils().send(inviter, "&c" + invited.getName() + " already has a pending invitation.");
            return;
        }

        pendingInvitations.put(invited.getUniqueId(), inviterParty.getId());
        inviterParty.sendMessage("&a" + invited.getName() + " has been invited to the party.");
        HomiesLib.getMessageUtils().send(invited, "&aYou have been invited to join " + inviterParty.getName() + "! Type /party accept " + inviter.getName() + " to join.");
        // TODO: Add a timeout for invitations using SchedulerService.task
    }

    /**
     * Accepts a party invitation.
     * The {@code player} will join the party of the {@code inviter} if a valid invitation exists.
     * @param player The {@link HomiesPlayer} accepting the invitation.
     * @param inviter The {@link HomiesPlayer} who sent the invitation.
     */
    @Override
    public void acceptInvitation(HomiesPlayer player, HomiesPlayer inviter) {
        Optional<Party> inviterPartyOpt = getPartyByPlayer(inviter);
        if (inviterPartyOpt.isEmpty()) {
            HomiesLib.getMessageUtils().send(player, "&cThe inviter is no longer in a party.");
            pendingInvitations.remove(player.getUniqueId());
            return;
        }
        Party inviterParty = inviterPartyOpt.get();

        if (!pendingInvitations.containsKey(player.getUniqueId()) || !pendingInvitations.get(player.getUniqueId()).equals(inviterParty.getId())) {
            HomiesLib.getMessageUtils().send(player, "&cYou do not have a pending invitation from " + inviter.getName() + ".");
            return;
        }

        if (getPartyByPlayer(player).isPresent()) {
            HomiesLib.getMessageUtils().send(player, "&cYou are already in a party.");
            pendingInvitations.remove(player.getUniqueId());
            return;
        }

        inviterParty.addMember(player);
        pendingInvitations.remove(player.getUniqueId());
        HomiesLib.getMessageUtils().send(player, "&aYou have joined " + inviterParty.getName() + "!");
    }

    /**
     * Declines a party invitation.
     * @param player The {@link HomiesPlayer} declining the invitation.
     * @param inviter The {@link HomiesPlayer} who sent the invitation.
     */
    @Override
    public void declineInvitation(HomiesPlayer player, HomiesPlayer inviter) {
        Optional<Party> inviterPartyOpt = getPartyByPlayer(inviter);
        if (inviterPartyOpt.isEmpty()) {
            HomiesLib.getMessageUtils().send(player, "&cThe inviter is no longer in a party.");
            pendingInvitations.remove(player.getUniqueId());
            return;
        }
        Party inviterParty = inviterPartyOpt.get();

        if (!pendingInvitations.containsKey(player.getUniqueId()) || !pendingInvitations.get(player.getUniqueId()).equals(inviterParty.getId())) {
            HomiesLib.getMessageUtils().send(player, "&cYou do not have a pending invitation from " + inviter.getName() + ".");
            return;
        }

        pendingInvitations.remove(player.getUniqueId());
        HomiesLib.getMessageUtils().send(player, "&cYou have declined the invitation to " + inviterParty.getName() + ".");
        inviterParty.sendMessage("&c" + player.getName() + " has declined the party invitation.");
    }

    /**
     * Kicks a member from a party.
     * The {@code leader} must be the leader of the party to perform a kick.
     * @param leader The {@link HomiesPlayer} who is the party leader performing the kick.
     * @param member The {@link HomiesPlayer} to kick from the party.
     */
    @Override
    public void kickMember(HomiesPlayer leader, HomiesPlayer member) {
        Optional<Party> partyOpt = getPartyByPlayer(leader);
        if (partyOpt.isEmpty()) {
            HomiesLib.getMessageUtils().send(leader, "&cYou are not in a party.");
            return;
        }
        Party party = partyOpt.get();

        if (!party.isLeader(leader)) {
            HomiesLib.getMessageUtils().send(leader, "&cOnly the party leader can kick members.");
            return;
        }

        if (!party.isMember(member)) {
            HomiesLib.getMessageUtils().send(leader, "&c" + member.getName() + " is not in your party.");
            return;
        }

        if (leader.equals(member)) {
            HomiesLib.getMessageUtils().send(leader, "&cYou cannot kick yourself. Use /party leave to leave the party.");
            return;
        }

        party.removeMember(member);
        HomiesLib.getMessageUtils().send(member, "&cYou have been kicked from " + party.getName() + ".");
        if (party.getSize() <= 1) { // If only leader remains, disband
            disbandParty(party);
        }
    }

    /**
     * Allows a player to leave their current party.
     * If the player is the leader and other members exist, leadership will be transferred.
     * If the party becomes empty, it will be disbanded.
     * @param player The {@link HomiesPlayer} leaving the party.
     */
    @Override
    public void leaveParty(HomiesPlayer player) {
        Optional<Party> partyOpt = getPartyByPlayer(player);
        if (partyOpt.isEmpty()) {
            HomiesLib.getMessageUtils().send(player, "&cYou are not in a party.");
            return;
        }
        Party party = partyOpt.get();

        party.removeMember(player);
        HomiesLib.getMessageUtils().send(player, "&aYou have left " + party.getName() + ".");
        if (party.getSize() == 0) {
            disbandParty(party);
        }
    }

    /**
     * Transfers leadership of a party from the current leader to another member.
     * The {@code currentLeader} must be the actual leader, and {@code newLeader} must be a member of the party.
     * @param currentLeader The {@link HomiesPlayer} who is the current leader.
     * @param newLeader The {@link HomiesPlayer} to transfer leadership to (must be a current member).
     */
    @Override
    public void transferLeadership(HomiesPlayer currentLeader, HomiesPlayer newLeader) {
        Optional<Party> partyOpt = getPartyByPlayer(currentLeader);
        if (partyOpt.isEmpty()) {
            HomiesLib.getMessageUtils().send(currentLeader, "&cYou are not in a party.");
            return;
        }
        Party party = partyOpt.get();

        if (!party.isLeader(currentLeader)) {
            HomiesLib.getMessageUtils().send(currentLeader, "&cYou are not the party leader.");
            return;
        }

        if (!party.isMember(newLeader)) {
            HomiesLib.getMessageUtils().send(currentLeader, "&c" + newLeader.getName() + " is not in your party.");
            return;
        }

        party.setLeader(newLeader);
    }
}
