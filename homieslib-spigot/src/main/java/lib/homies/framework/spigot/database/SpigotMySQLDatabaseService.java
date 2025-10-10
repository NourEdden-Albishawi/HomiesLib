package lib.homies.framework.spigot.database;

import lib.homies.framework.database.DatabaseService;
import lib.homies.framework.database.IRepository;
import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import lib.homies.framework.spigot.HomiesLibSpigot;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SpigotMySQLDatabaseService implements DatabaseService {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final List<Class<?>> entityClasses;
    private Connection connection;

    public SpigotMySQLDatabaseService(String host, int port, String database, String username, String password, List<Class<?>> entityClasses) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.entityClasses = entityClasses;
    }

    public boolean init() {
        if (host == null || host.isEmpty()) {
            return true; // Dummy service is not a failure
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";
            this.connection = DriverManager.getConnection(url, username, password);
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Successfully connected to MySQL database.");
            createTablesIfNotExist();
            return true;
        } catch (ClassNotFoundException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "MySQL JDBC Driver not found! The MySQL service will be disabled.", e);
            this.connection = null;
            return false;
        } catch (SQLException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().warning(
                    "Failed to connect to MySQL database at '" + host + ":" + port + "'. " +
                            "Please check your configuration and ensure the database is running. The MySQL service will be disabled."
            );
            this.connection = null;
            return false;
        }
    }

    public void onDisable() {
        if (connection != null) {
            try {
                connection.close();
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Disconnected from MySQL database.");
            } catch (SQLException e) {
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to close MySQL connection!", e);
            }
        }
    }

    private void createTablesIfNotExist() {
        if (entityClasses == null || entityClasses.isEmpty()) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("No @DbEntity classes found for automatic table creation.");
            return;
        }

        for (Class<?> entityClass : entityClasses) {
            DbEntity dbEntity = entityClass.getAnnotation(DbEntity.class);
            if (dbEntity == null) {
                continue;
            }

            String tableName = dbEntity.tableName().isEmpty() ? entityClass.getSimpleName() : dbEntity.tableName();
            StringBuilder sqlBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `")
                    .append(tableName).append("` (");

            List<String> columnDefinitions = new ArrayList<>();

            for (Field field : entityClass.getDeclaredFields()) {
                DbField dbField = field.getAnnotation(DbField.class);
                if (dbField != null) {
                    String columnName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                    String sqlType = getSqlType(field.getType());
                    if (sqlType == null) {
                        HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().warning(
                                "Unsupported SQL type for field '" + field.getName() + "' in entity '" + entityClass.getSimpleName() + "'. Skipping column."
                        );
                        continue;
                    }

                    String definition = "`" + columnName + "` " + sqlType;
                    if (dbField.id()) {
                        definition += " PRIMARY KEY";
                    }
                    columnDefinitions.add(definition);
                }
            }

            if (columnDefinitions.isEmpty()) {
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().warning(
                        "No @DbField annotated fields found for entity '" + entityClass.getSimpleName() + "'. Skipping table creation."
                );
                continue;
            }

            sqlBuilder.append(String.join(", ", columnDefinitions));
            sqlBuilder.append(");");

            try (Statement statement = connection.createStatement()) {
                statement.execute(sqlBuilder.toString());
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Ensured table '" + tableName + "' exists for entity '" + entityClass.getSimpleName() + "'.");
            } catch (SQLException e) {
                HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE,
                        "Failed to create/verify table '" + tableName + "' for entity '" + entityClass.getSimpleName() + "'.", e);
            }
        }
    }

    private String getSqlType(Class<?> javaType) {
        if (javaType == String.class) {
            return "VARCHAR(255)";
        } else if (javaType == int.class || javaType == Integer.class) {
            return "INT";
        } else if (javaType == long.class || javaType == Long.class) {
            return "BIGINT";
        } else if (javaType == double.class || javaType == Double.class) {
            return "DOUBLE";
        } else if (javaType == boolean.class || javaType == Boolean.class) {
            return "BOOLEAN";
        } else if (javaType == UUID.class) {
            return "VARCHAR(36)";
        } else if (javaType == LocalDateTime.class) {
            return "DATETIME";
        }
        return null;
    }

    private String getTableName(Class<?> entityType) {
        DbEntity dbEntity = entityType.getAnnotation(DbEntity.class);
        if (dbEntity == null) {
            throw new IllegalArgumentException("Class " + entityType.getName() + " is not annotated with @DbEntity.");
        }
        return dbEntity.tableName().isEmpty() ? entityType.getSimpleName() : dbEntity.tableName();
    }

    private Field getIdField(Class<?> entityType) {
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbField.class) && field.getAnnotation(DbField.class).id()) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalStateException("Entity " + entityType.getName() + " does not have an ID field annotated with @DbField(id=true).");
    }

    @Override
    public <T extends IRepository<?>> Optional<T> getRepository(Class<T> repoClass) {
        return Optional.empty();
    }

    @Override
    public <T> void saveEntity(T entity) {
        if (connection == null) {
            return;
        }

        Class<?> entityType = entity.getClass();
        String tableName = getTableName(entityType);

        List<String> columnNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbField.class)) {
                DbField dbField = field.getAnnotation(DbField.class);
                field.setAccessible(true);
                try {
                    columnNames.add(dbField.columnName().isEmpty() ? field.getName() : dbField.columnName());
                    Object value = field.get(entity);
                    if (value instanceof UUID) {
                        value = value.toString();
                    } else if (value instanceof LocalDateTime) {
                        value = Timestamp.valueOf((LocalDateTime) value);
                    }
                    values.add(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field " + field.getName() + " in entity " + entityType.getName(), e);
                }
            }
        }

        String columns = columnNames.stream().map(c -> "`" + c + "`").collect(Collectors.joining(", "));
        String placeholders = String.join(", ", Collections.nCopies(columnNames.size(), "?"));
        String updateSet = columnNames.stream()
                .map(col -> "`" + col + "` = ?")
                .collect(Collectors.joining(", "));

        String insertSql = "INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + placeholders + ") ON DUPLICATE KEY UPDATE " + updateSet;

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            int i = 1;
            for (Object value : values) {
                statement.setObject(i++, value);
            }
            for (Object value : values) {
                statement.setObject(i++, value);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to save entity to MySQL", e);
        }
    }

    @Override
    public <T> Optional<T> findById(Class<T> entityType, String id) {
        Field idField = getIdField(entityType);
        DbField dbField = idField.getAnnotation(DbField.class);
        String idColumnName = dbField.columnName().isEmpty() ? idField.getName() : dbField.columnName();
        return findByField(entityType, idColumnName, id).stream().findFirst();
    }

    private boolean isValidColumn(Class<?> entityType, String columnName) {
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbField.class)) {
                DbField dbField = field.getAnnotation(DbField.class);
                String actualColumnName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                if (actualColumnName.equals(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setStatementValue(PreparedStatement statement, int index, Object value) throws SQLException {
        if (value instanceof UUID) {
            statement.setString(index, value.toString());
        } else {
            statement.setObject(index, value);
        }
    }

    @Override
    public <T> List<T> findByField(Class<T> entityType, String fieldName, Object value) {
        if (connection == null) {
            return Collections.emptyList();
        }

        if (!isValidColumn(entityType, fieldName)) {
            String errorMessage = "Attempted to query by an invalid or non-DB field: " + fieldName + " for entity " + entityType.getSimpleName();
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String tableName = getTableName(entityType);
        String sql = "SELECT * FROM `" + tableName + "` WHERE `" + fieldName + "` = ?";
        List<T> entities = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStatementValue(statement, 1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(mapResultSetToEntity(resultSet, entityType));
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to find entities by field '" + fieldName + "' from MySQL", e);
        }
        return entities;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityType) {
        if (connection == null) {
            return Collections.emptyList();
        }

        String tableName = getTableName(entityType);
        String sql = "SELECT * FROM `" + tableName + "`";
        List<T> entities = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                entities.add(mapResultSetToEntity(resultSet, entityType));
            }
        } catch (SQLException | ReflectiveOperationException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to find all or instantiate entities from MySQL", e);
        }
        return entities;
    }

    @Override
    public <T> void deleteEntity(T entity) {
        if (connection == null) {
            return;
        }

        Class<?> entityType = entity.getClass();
        String tableName = getTableName(entityType);
        Field idField = getIdField(entityType);
        try {
            idField.setAccessible(true);
            Object idValue = idField.get(entity);
            DbField dbField = idField.getAnnotation(DbField.class);
            String idColumnName = dbField.columnName().isEmpty() ? idField.getName() : dbField.columnName();

            // This dynamic SQL is safe because idColumnName is derived from the annotation, not user input.
            String sql = "DELETE FROM `" + tableName + "` WHERE `" + idColumnName + "` = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, idValue.toString());
                statement.executeUpdate();
            }
        } catch (SQLException | IllegalAccessException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to delete entity from MySQL", e);
        }
    }

    @Override
    public <T> void deleteByField(Class<T> entityType, String fieldName, Object value) {
        if (connection == null) {
            return;
        }

        if (!isValidColumn(entityType, fieldName)) {
            String errorMessage = "Attempted to delete by an invalid or non-DB field: " + fieldName + " for entity " + entityType.getSimpleName();
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String tableName = getTableName(entityType);
        String sql = "DELETE FROM `" + tableName + "` WHERE `" + fieldName + "` = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStatementValue(statement, 1, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to delete entities by field '" + fieldName + "' from MySQL", e);
        }
    }

    private <T> T mapResultSetToEntity(ResultSet resultSet, Class<T> entityType) throws SQLException, ReflectiveOperationException {
        T entity = entityType.getDeclaredConstructor().newInstance();
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbField.class)) {
                DbField dbField = field.getAnnotation(DbField.class);
                field.setAccessible(true);
                String columnName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                Object value = resultSet.getObject(columnName);

                if (value != null) {
                    if (field.getType() == UUID.class && value instanceof String) {
                        value = UUID.fromString((String) value);
                    } else if (field.getType() == LocalDateTime.class && value instanceof Timestamp) {
                        value = ((Timestamp) value).toLocalDateTime();
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        value = resultSet.getBoolean(columnName);
                    } else if (field.getType().isEnum() && value instanceof String) {
                        // This cast is safe because we've checked isEnum().
                        // Suppressing the rawtypes warning is a common practice for this pattern.
                        @SuppressWarnings("rawtypes")
                        Class enumType = field.getType();
                        value = Enum.valueOf(enumType, (String) value);
                    }
                }
                field.set(entity, value);
            }
        }
        return entity;
    }
}
