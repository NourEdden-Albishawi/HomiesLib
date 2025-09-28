package lib.homies.framework.playerdata;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Optional;

/**
 * A platform-agnostic interface for managing player-specific data.
 * This service allows framework developers to store and retrieve {@link PlayerData} for {@link HomiesPlayer}s.
 */
public interface PlayerDataService {

    /**
     * Retrieves the {@link PlayerData} for a given {@link HomiesPlayer}.
     * This method uses the player's unique ID to fetch data from the database.
     * @param player The {@link HomiesPlayer} whose data is to be retrieved.
     * @return An {@link Optional} containing the {@link PlayerData} if found, otherwise {@link Optional#empty()}.
     */
    Optional<PlayerData> getPlayerData(HomiesPlayer player);

    /**
     * Saves the provided {@link PlayerData} object to the database.
     * This will either create new data or update existing data based on the player's UUID.
     * @param playerData The {@link PlayerData} object to save.
     */
    void savePlayerData(PlayerData playerData);

    /**
     * Retrieves the {@link PlayerData} for a given player UUID.
     * @param uuid The {@link String} representation of the UUID of the player whose data is to be retrieved.
     * @return An {@link Optional} containing the {@link PlayerData} if found, otherwise {@link Optional#empty()}.
     */
    Optional<PlayerData> getPlayerData(String uuid);
}
