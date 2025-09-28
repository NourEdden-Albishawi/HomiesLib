package lib.homies.framework.playerdata;

import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents platform-agnostic player data to be stored in the database.
 * This entity is designed to be persisted using the {@link lib.homies.framework.database.DatabaseService}.
 */
@Data
@NoArgsConstructor // Required for reflection-based instantiation by the database service
@DbEntity(tableName = "player_data")
public class PlayerData {

    /**
     * The unique identifier of the player.
     * This field is marked as the primary ID for the entity.
     */
    @DbField(columnName = "uuid", id = true)
    private String uuid;

    /**
     * The timestamp of the player's last login.
     */
    @DbField(columnName = "last_login")
    private long lastLogin;

    // You can add more generic player data fields here as needed.
}
