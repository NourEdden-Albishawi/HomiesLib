package dev.al3mid3x.lib.entities;


import lombok.Data;

import java.util.UUID;

@Data
public class LibPlayer {

    private final UUID uuid;

    private final String name;

    private int kills;

    private int deaths;

    public int AddKills(int count) {
        setKills(this.kills + count);
        return this.kills + count;
    }

    public int AddDeaths(int count) {
        setKills(this.deaths + count);
        return this.deaths + count;
    }
}

