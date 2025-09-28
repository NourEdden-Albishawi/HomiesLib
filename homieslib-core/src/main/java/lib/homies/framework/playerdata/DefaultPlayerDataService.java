package lib.homies.framework.playerdata;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;

import java.util.Optional;

/**
 * Default, platform-independent implementation of the {@link PlayerDataService}.
 * This service resides in the core module and leverages the framework's {@link lib.homies.framework.database.DatabaseService}
 * to store and retrieve {@link PlayerData} entities.
 */
public class DefaultPlayerDataService implements PlayerDataService {

    /**
     * Retrieves the {@link PlayerData} for a given {@link HomiesPlayer}.
     * This method delegates to {@link #getPlayerData(String)} using the player's unique ID.
     * @param player The {@link HomiesPlayer} whose data is to be retrieved.
     * @return An {@link Optional} containing the {@link PlayerData} if found, otherwise {@link Optional#empty()}.
     */
    @Override
    public Optional<PlayerData> getPlayerData(HomiesPlayer player) {
        return getPlayerData(player.getUniqueId().toString());
    }

    /**
     * Saves the provided {@link PlayerData} object to the database.
     * This method uses the framework's {@link lib.homies.framework.database.DatabaseService#saveEntity(Object)}
     * to persist the data.
     * @param playerData The {@link PlayerData} object to save.
     */
    @Override
    public void savePlayerData(PlayerData playerData) {
        HomiesLib.getDatabaseService().saveEntity(playerData);
    }

    /**
     * Retrieves the {@link PlayerData} for a given player UUID.
     * This method uses the framework's {@link lib.homies.framework.database.DatabaseService#findById(Class, String)}
     * to fetch the data.
     * @param uuid The {@link String} representation of the UUID of the player whose data is to be retrieved.
     * @return An {@link Optional} containing the {@link PlayerData} if found, otherwise {@link Optional#empty()}.
     */
    @Override
    public Optional<PlayerData> getPlayerData(String uuid) {
        return HomiesLib.getDatabaseService().findById(PlayerData.class, uuid);
    }
}
