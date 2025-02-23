package dev.al3mid3x.lib.configurations;

@ConfigFile(fileName = "database.yml")
public class DatabaseConfig {
    @ConfigKey(path = "type", comment = "Database type (mysql, sqlite)")
    public String type = "sqlite"; // Default to SQLite

    @ConfigKey(path = "mysql.url", comment = "MySQL connection URL")
    public String mysqlUrl = "jdbc:mysql://localhost:3306/minecraft_db";

    @ConfigKey(path = "mysql.username", comment = "MySQL username")
    public String mysqlUsername = "root";

    @ConfigKey(path = "mysql.password", comment = "MySQL password")
    public String mysqlPassword = "password";

    @ConfigKey(path = "sqlite.path", comment = "SQLite database file path")
    public String sqlitePath = "plugins/homies-lib/database.db";
}