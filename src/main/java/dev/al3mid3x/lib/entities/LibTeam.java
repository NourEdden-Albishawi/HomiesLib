package dev.al3mid3x.lib.entities;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LibTeam {
    private final UUID uuid;
    private final String name;
    private final UUID founderId;
    private final List<UUID> moderators = Lists.newArrayList();
    private final List<UUID> members = Lists.newArrayList();
}
