package lib.homies.framework.spigot;

import lib.homies.framework.config.annotations.ConfigFile;
import lib.homies.framework.config.annotations.ConfigKey;

@ConfigFile(fileName = "config.yml")
public class HomiesLibConfig {

    @ConfigKey(path = "database.sqlite-fallback-enabled", comment = "If Redis and MySQL fail, should the plugin fall back to a local SQLite database? Default: true")
    public boolean sqliteFallbackEnabled = true;

    @ConfigKey(path = "database.redis.enabled")
    public boolean redisEnabled = false;

    @ConfigKey(path = "database.redis.host")
    public String redisHost = "localhost";

    @ConfigKey(path = "database.redis.port")
    public int redisPort = 6379;

    @ConfigKey(path = "database.redis.password", comment = "Leave empty if no password")
    public String redisPassword = "";

    @ConfigKey(path = "database.redis.database")
    public int redisDatabase = 0;

    @ConfigKey(path = "database.mysql.enabled")
    public boolean mysqlEnabled = false;

    @ConfigKey(path = "database.mysql.host")
    public String mysqlHost = "localhost";

    @ConfigKey(path = "database.mysql.port")
    public int mysqlPort = 3306;

    @ConfigKey(path = "database.mysql.database")
    public String mysqlDatabase = "homieslib";

    @ConfigKey(path = "database.mysql.username")
    public String mysqlUsername = "root";

    @ConfigKey(path = "database.mysql.password")
    public String mysqlPassword = "";
}
