package lib.homies.framework.spigot;

import lib.homies.framework.config.annotations.ConfigFile;
import lib.homies.framework.config.annotations.ConfigKey;

@ConfigFile(fileName = "config.yml")
public class HomiesLibConfig {

    @ConfigKey(path = "redis.host")
    public String redisHost = "localhost";

    @ConfigKey(path = "redis.port")
    public int redisPort = 6379;

    @ConfigKey(path = "redis.password")
    public String redisPassword = ""; // Leave empty if no password

    @ConfigKey(path = "redis.database")
    public int redisDatabase = 0;
}
