package dev.al3mid3x.lib.configurations;

@ConfigFile(fileName = "config.yml")
public class MainConfig {
    @ConfigKey(path = "database.url", comment = "The database connection URL")
    public String databaseUrl = "jdbc:mysql://localhost:3306/minecraft_db";

    @ConfigKey(path = "database.username", comment = "The database username")
    public String databaseUsername = "root";

    @ConfigKey(path = "database.password", comment = "The database password")
    public String databasePassword = "password";

}