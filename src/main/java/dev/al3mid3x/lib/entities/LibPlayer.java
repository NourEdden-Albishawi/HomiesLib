package dev.al3mid3x.lib.entities;


import lombok.Data;

import java.util.UUID;

@Data
public class LibPlayer {

    private final UUID uuid;

    private final String name;

    private UUID teamId;

    private int kills;

    private int deaths;

    public int addKills(int count) {
        setKills(this.kills + count);
        return this.kills + count;
    }

    public int addDeaths(int count) {
        setKills(this.deaths + count);
        return this.deaths + count;
    }

    public int removeKills(int count) {
        setKills(this.kills - count);
        return this.kills - count;
    }

    public int removeDeaths(int count) {
        setKills(this.deaths - count);
        return this.deaths - count;
    }
}

