package lib.homies.framework.spigot.database;

import lib.homies.framework.PluginContext;
import lib.homies.framework.database.DatabaseService;
import lib.homies.framework.database.IRepository;
import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import lib.homies.framework.spigot.HomiesLibSpigot;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

public class SpigotSQLiteDatabaseService implements DatabaseService {

    private final List<Class<?>> entityClasses;
    private final PluginContext pluginContext;
    private Connection connection;

    public SpigotSQLiteDatabaseService(PluginContext pluginContext, List<Class<?>> entityClasses) {
        this.pluginContext = pluginContext;
        this.entityClasses = entityClasses;
    }

    public boolean init() {
        File dbFile = new File(pluginContext.getDataFolder(), "database.db");
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            this.connection = DriverManager.getConnection(url);
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Successfully connected to SQLite database.");
            createTablesIfNotExist();
            return true;
        } catch (ClassNotFoundException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "SQLite JDBC Driver not found! The SQLite service will be disabled.", e);
            this.connection = null;
            return false;
        } catch (SQLException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to connect to SQLite database!", e);
            this.connection = null;
            return false;
        }
    }

    public void onDisable() {
        if (connection != null) {
            try {
                connection.close();
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Disconnected from SQLite database.");
            } catch (SQLException e) {
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to close SQLite connection!", e);
            }
        }
    }

    private void createTablesIfNotExist() {
        if (entityClasses == null || entityClasses.isEmpty()) return;
        for (Class<?> entityClass : entityClasses) {
            String tableName = getTableName(entityClass);
            StringBuilder sqlBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
            List<String> columnDefinitions = new ArrayList<>();
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(DbField.class)) {
                    DbField dbField = field.getAnnotation(DbField.class);
                    String columnName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                    String sqlType = getSqlType(field.getType());
                    String definition = "`" + columnName + "` " + sqlType;
                    if (dbField.id()) {
                        definition += " PRIMARY KEY";
                    }
                    columnDefinitions.add(definition);
                }
            }
            sqlBuilder.append(String.join(", ", columnDefinitions)).append(");");
            try (Statement statement = connection.createStatement()) {
                statement.execute(sqlBuilder.toString());
            } catch (SQLException e) {
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to create table for entity '" + entityClass.getSimpleName() + "'.", e);
            }
        }
    }

    private String getSqlType(Class<?> javaType) {
        if (javaType == String.class || javaType == UUID.class || javaType == LocalDateTime.class) return "TEXT";
        if (javaType == int.class || javaType == Integer.class || javaType == long.class || javaType == Long.class || javaType == boolean.class || javaType == Boolean.class)
            return "INTEGER";
        if (javaType == double.class || javaType == Double.class) return "REAL";
        return "BLOB";
    }

    private String getTableName(Class<?> entityType) {
        return entityType.getAnnotation(DbEntity.class).tableName().isEmpty() ? entityType.getSimpleName() : entityType.getAnnotation(DbEntity.class).tableName();
    }

    @Override
    public <T extends IRepository<?>> Optional<T> getRepository(Class<T> repoClass) {
        return Optional.empty();
    }

    @Override
    public <T> void saveEntity(T entity) {
        if (connection == null) return;
        String tableName = getTableName(entity.getClass());
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DbField.class)) {
                field.setAccessible(true);
                try {
                    columns.add(field.getAnnotation(DbField.class).columnName().isEmpty() ? field.getName() : field.getAnnotation(DbField.class).columnName());
                    Object value = field.get(entity);
                    if (value instanceof UUID || value instanceof LocalDateTime) values.add(value.toString());
                    else if (value instanceof Boolean) values.add((Boolean) value ? 1 : 0);
                    else values.add(value);
                } catch (IllegalAccessException e) { /* ... */ }
            }
        }
        String placeholders = String.join(",", Collections.nCopies(columns.size(), "?"));
        String sql = "INSERT OR REPLACE INTO `" + tableName + "` (`" + String.join("`, `", columns) + "`) VALUES (" + placeholders + ");";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to save entity to SQLite", e);
        }
    }

    @Override
    public <T> Optional<T> findById(Class<T> entityType, String id) {
        String idColumnName = getIdField(entityType).getAnnotation(DbField.class).columnName();
        if (idColumnName.isEmpty()) {
            idColumnName = getIdField(entityType).getName();
        }
        return findByField(entityType, idColumnName, id).stream().findFirst();
    }

    @Override
    public <T> List<T> findByField(Class<T> entityType, String fieldName, Object value) {
        if (connection == null) return Collections.emptyList();
        String tableName = getTableName(entityType);
        String sql = "SELECT * FROM `" + tableName + "` WHERE `" + fieldName + "` = ?";
        List<T> entities = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (value instanceof UUID) statement.setString(1, value.toString());
            else statement.setObject(1, value);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    entities.add(mapResultSetToEntity(rs, entityType));
                }
            }
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to find entities by field from SQLite", e);
        }
        return entities;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityType) {
        if (connection == null) return Collections.emptyList();
        String tableName = getTableName(entityType);
        String sql = "SELECT * FROM `" + tableName + "`";
        List<T> entities = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs, entityType));
            }
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to find all entities from SQLite", e);
        }
        return entities;
    }

    @Override
    public <T> void deleteEntity(T entity) {
        if (connection == null) return;
        String tableName = getTableName(entity.getClass());
        Field idField = getIdField(entity.getClass());
        try {
            idField.setAccessible(true);
            Object idValue = idField.get(entity);
            String idColumnName = idField.getAnnotation(DbField.class).columnName();
            if (idColumnName.isEmpty()) {
                idColumnName = idField.getName();
            }
            String sql = "DELETE FROM `" + tableName + "` WHERE `" + idColumnName + "` = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setObject(1, idValue.toString());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to delete entity from SQLite", e);
        }
    }

    @Override
    public <T> void deleteByField(Class<T> entityType, String fieldName, Object value) {
        if (connection == null) return;
        String tableName = getTableName(entityType);
        String sql = "DELETE FROM `" + tableName + "` WHERE `" + fieldName + "` = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (value instanceof UUID) ps.setString(1, value.toString());
            else ps.setObject(1, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to delete entities by field from SQLite", e);
        }
    }

    private <T> T mapResultSetToEntity(ResultSet rs, Class<T> entityType) throws Exception {
        T entity = entityType.getDeclaredConstructor().newInstance();
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbField.class)) {
                field.setAccessible(true);
                String columnName = field.getAnnotation(DbField.class).columnName().isEmpty() ? field.getName() : field.getAnnotation(DbField.class).columnName();
                Object value = rs.getObject(columnName);
                if (value != null) {
                    if (field.getType() == UUID.class) field.set(entity, UUID.fromString(value.toString()));
                    else if (field.getType() == LocalDateTime.class)
                        field.set(entity, LocalDateTime.parse(value.toString()));
                    else if (field.getType() == boolean.class || field.getType() == Boolean.class)
                        field.set(entity, ((Number) value).intValue() == 1);
                    else field.set(entity, value);
                }
            }
        }
        return entity;
    }

    private Field getIdField(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(DbField.class) && f.getAnnotation(DbField.class).id())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ID field found on entity " + entityType.getSimpleName()));
    }
}
