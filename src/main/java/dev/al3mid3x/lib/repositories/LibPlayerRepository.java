package dev.al3mid3x.lib.repositories;


import dev.al3mid3x.lib.entities.LibPlayer;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRepository {
    private final Connection connection;
    private final Map<UUID, LibPlayer> cache = new HashMap<>();

    public PlayerRepository(Connection connection) {
        this.connection = connection;
    }

    public void loadAll() throws SQLException {
        String query = "SELECT * FROM player_data";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                LibPlayer player = new LibPlayer(UUID.fromString(rs.getString("uuid")),rs.getString("name"));
                player.setKills(rs.getInt("kills"));
                player.setDeaths(rs.getInt("deaths"));
                cache.put(player.getUuid(), player);
            }
        }
    }

    public void saveAll() throws SQLException {
        String query = "INSERT INTO player_data (uuid, name, kills, deaths) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET name = ?, kills = ?, deaths = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (LibPlayer player : cache.values()) {
                stmt.setString(1, player.getUuid().toString());
                stmt.setString(2, player.getName());
                stmt.setInt(3, player.getKills());
                stmt.setDouble(4, player.getDeaths());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public LibPlayer getByUuid(UUID uuid) {
        return cache.get(uuid);
    }

    public void addOrUpdate(LibPlayer player) {
        cache.put(player.getUuid(), player);
    }
}