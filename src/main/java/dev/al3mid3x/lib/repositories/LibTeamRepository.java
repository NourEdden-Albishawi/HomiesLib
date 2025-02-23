package dev.al3mid3x.lib.repositories;

import dev.al3mid3x.lib.entities.LibTeam;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LibTeamRepository {
    private final Connection connection;
    private final Map<UUID, LibTeam> cache = new HashMap<>();

    public LibTeamRepository(Connection connection) {
        this.connection = connection;
    }

    public void loadAll() throws SQLException {
        String query = "SELECT * FROM teams";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                LibTeam team = new LibTeam(UUID.fromString(rs.getString("uuid")),rs.getString("name"),UUID.fromString(rs.getString("founderId")));
                cache.put(team.getUuid(), team);
            }
        }
    }

    public void saveAll() throws SQLException {
        String query = "INSERT INTO teams (id, name, founderId) VALUES (?, ?, ?) " +
                "ON CONFLICT(id) DO UPDATE SET name = ?, founderId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (LibTeam team : cache.values()) {
                stmt.setString(1, team.getUuid().toString());
                stmt.setString(2, team.getName());
                stmt.setString(3, team.getFounderId().toString());
                stmt.setString(4, team.getName());
                stmt.setString(5, team.getFounderId().toString());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public LibTeam getById(int id) {
        return cache.get(id);
    }

    public void addOrUpdate(LibTeam team) {
        cache.put(team.getUuid(), team);
    }
}