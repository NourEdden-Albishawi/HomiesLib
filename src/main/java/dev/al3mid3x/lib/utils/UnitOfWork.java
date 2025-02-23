package dev.al3mid3x.lib.utils;


import dev.al3mid3x.lib.gui.MenuManager;
import dev.al3mid3x.lib.repositories.LibPlayerRepository;
import dev.al3mid3x.lib.repositories.LibTeamRepository;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
public class UnitOfWork {
    private final Connection connection;
    private final LibPlayerRepository playerRepository;
    private final LibTeamRepository teamRepository;

    public UnitOfWork(Connection connection) {
        this.connection = connection;
        this.playerRepository = new LibPlayerRepository(connection);
        this.teamRepository = new LibTeamRepository(connection);}

    public void loadAll() throws SQLException {
        playerRepository.loadAll();
        teamRepository.loadAll();
    }

    public void saveAll() throws SQLException {
        playerRepository.saveAll();
        teamRepository.saveAll();
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
