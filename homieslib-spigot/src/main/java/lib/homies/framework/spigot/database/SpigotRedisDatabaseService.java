package lib.homies.framework.spigot.database;

import lib.homies.framework.database.DatabaseService;
import lib.homies.framework.database.IRepository;
import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Spigot-specific implementation of the {@link DatabaseService} interface.
 * This class provides an ORM-like layer for Redis, handling entity persistence
 * and retrieval based on {@link DbEntity} and {@link DbField} annotations.
 * It also manages {@link IRepository} instances via Java's ServiceLoader mechanism.
 */
public class SpigotRedisDatabaseService implements DatabaseService {

    private final JedisPool connectionPool;
    private final Map<Class<?>, IRepository<?>> repositories = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger(SpigotRedisDatabaseService.class.getName());

    /**
     * Constructs a new SpigotRedisDatabaseService with configurable Redis connection details.
     * @param host The Redis server host.
     * @param port The Redis server port.
     * @param password The Redis server password (can be empty).
     * @param database The Redis database index.
     */
    public SpigotRedisDatabaseService(String host, int port, String password, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // You might want to configure min/max idle, max total connections, etc. here
        // For simplicity, using default pool config for now.

        if (password != null && !password.isEmpty()) {
            this.connectionPool = new JedisPool(poolConfig, host, port, 2000, password, database);
        } else {
            this.connectionPool = new JedisPool(poolConfig, host, port, 2000, null, database);
        }
    }

    /**
     * Initializes the database service by loading and instantiating registered repositories.
     * This method should be called during plugin enable.
     */
    public void init() {
        loadRepositories();
    }

    /**
     * Performs cleanup operations when the plugin is disabled.
     * Closes the Redis connection pool and clears registered repositories.
     */
    public void onDisable() {
        if (connectionPool != null) {
            connectionPool.close();
        }
        repositories.clear();
    }

    /**
     * Loads {@link IRepository} implementations using Java's ServiceLoader.
     * Repositories are expected to have a constructor that takes this service as an argument.
     */
    private void loadRepositories() {
        ServiceLoader<IRepository> loader = ServiceLoader.load(IRepository.class);
        for (IRepository repo : loader) {
            try {
                // Assumes repositories have a constructor that takes this service
                Constructor<? extends IRepository> constructor = repo.getClass().getConstructor(SpigotRedisDatabaseService.class);
                IRepository instantiatedRepo = constructor.newInstance(this);

                Class<?> entityType = getRepositoryEntityType(repo.getClass());
                if (entityType != null) {
                    repositories.put(entityType, instantiatedRepo);
                    logger.log(Level.INFO, "[UoW] Registered repository for entity: " + entityType.getSimpleName());
                } else {
                    logger.log(Level.WARNING, "[UoW] Could not determine entity type for repository: " + repo.getClass().getName());
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[UoW] Failed to instantiate repository: " + repo.getClass().getName(), e);
            }
        }
    }

    /**
     * Retrieves a registered repository by its class.
     * @param repoClass The class of the repository to retrieve.
     * @return An {@link Optional} containing the repository if found, otherwise {@link Optional#empty()}.
     * @param <T> The type of the repository, which must extend {@link IRepository}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IRepository<?>> Optional<T> getRepository(Class<T> repoClass) {
        // This logic might need adjustment based on how you map repo interfaces to entity types
        for (IRepository<?> repo : repositories.values()) {
            if (repoClass.isInstance(repo)) {
                return Optional.of((T) repo);
            }
        }
        return Optional.empty();
    }

    // --- ORM-like methods for direct entity manipulation ---

    /**
     * Saves an entity to Redis using reflection and annotations.
     * The entity's class must be annotated with {@link DbEntity} and have an ID field
     * annotated with {@link DbField}(id=true).
     *
     * @param entity The entity object to save.
     * @param <T> The entity type.
     * @throws IllegalArgumentException if the entity class is not annotated with @DbEntity.
     * @throws IllegalStateException if the entity does not have an ID field.
     * @throws RuntimeException if saving fails due to reflection errors or database issues.
     */
    @Override
    public <T> void saveEntity(T entity) {
        Class<?> clazz = entity.getClass();
        DbEntity dbEntity = getDbEntityAnnotation(clazz);
        String keyPrefix = dbEntity.tableName();

        String entityId = getEntityId(entity);
        if (entityId == null) {
            throw new IllegalStateException("Cannot save entity without a field annotated with @DbField(id=true).");
        }

        Map<String, String> data = new HashMap<>();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(DbField.class)) {
                    field.setAccessible(true);
                    DbField dbField = field.getAnnotation(DbField.class);
                    String columnName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                    Object value = field.get(entity);
                    if (value != null) {
                        data.put(columnName, String.valueOf(value));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access entity field during save.", e);
        }

        try (Jedis jedis = connectionPool.getResource()) {
            jedis.hset(keyPrefix + ":" + entityId, data);
        }
    }

    /**
     * Finds and retrieves an entity from Redis by its ID.
     * The entity's class must be annotated with {@link DbEntity}.
     *
     * @param entityType The class of the entity to find.
     * @param id         The ID of the entity.
     * @param <T>        The entity type.
     * @return An {@link Optional} containing the found entity, or {@link Optional#empty()} if not found.
     * @throws IllegalArgumentException if the entityType is not annotated with @DbEntity.
     * @throws RuntimeException if retrieval or mapping fails due to reflection errors or database issues.
     */
    @Override
    public <T> Optional<T> findById(Class<T> entityType, String id) {
        DbEntity dbEntity = getDbEntityAnnotation(entityType);
        String keyPrefix = dbEntity.tableName();

        try (Jedis jedis = connectionPool.getResource()) {
            Map<String, String> data = jedis.hgetAll(keyPrefix + ":" + id);
            if (data.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(mapToEntity(entityType, data));
        }
    }

    /**
     * Retrieves the {@link DbEntity} annotation from a class, throwing an exception if not present.
     * @param clazz The class to inspect.
     * @return The {@link DbEntity} annotation instance.
     * @param <T> The type of the class.
     * @throws IllegalArgumentException if the class is not annotated with @DbEntity.
     */
    private <T> DbEntity getDbEntityAnnotation(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(DbEntity.class)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @DbEntity.");
        }
        return clazz.getAnnotation(DbEntity.class);
    }

    /**
     * Extracts the ID field value from an entity object.
     * @param entity The entity instance.
     * @return The string representation of the ID, or {@code null} if no ID field is found or its value is null.
     * @param <T> The type of the entity.
     * @throws RuntimeException if accessing the ID field fails.
     */
    private <T> String getEntityId(T entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DbField.class) && field.getAnnotation(DbField.class).id()) {
                field.setAccessible(true);
                try {
                    Object idValue = field.get(entity);
                    return idValue != null ? String.valueOf(idValue) : null;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access ID field for entity " + entity.getClass().getSimpleName(), e);
                }
            }
        }
        return null;
    }

    /**
     * Maps a Redis hash (Map<String, String>) to an entity object.
     * @param entityType The class of the entity to map to.
     * @param data The Redis hash data.
     * @return An instance of the entityType populated with data.
     * @param <T> The type of the entity.
     * @throws RuntimeException if mapping fails due to reflection errors.
     */
    private <T> T mapToEntity(Class<T> entityType, Map<String, String> data) {
        try {
            T entity = entityType.getDeclaredConstructor().newInstance();
            for (Field field : entityType.getDeclaredFields()) {
                if (field.isAnnotationPresent(DbField.class)) {
                    field.setAccessible(true);
                    DbField dbField = field.getAnnotation(DbField.class);
                    String columnName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                    String value = data.get(columnName);
                    if (value != null) {
                        setFieldValue(field, entity, value);
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map Redis hash to entity.", e);
        }
    }

    /**
     * Sets the value of a field on an entity object, handling type conversions.
     * @param field The field to set.
     * @param entity The entity object.
     * @param value The string value to convert and set.
     * @throws IllegalAccessException if the field cannot be accessed.
     */
    private void setFieldValue(Field field, Object entity, String value) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == String.class) field.set(entity, value);
        else if (type == int.class || type == Integer.class) field.set(entity, Integer.parseInt(value));
        else if (type == double.class || type == Double.class) field.set(entity, Double.parseDouble(value));
        else if (type == boolean.class || type == Boolean.class) field.set(entity, Boolean.parseBoolean(value));
        // Add more type conversions as needed
    }

    /**
     * Determines the entity type managed by a given repository class.
     * This is typically the generic type parameter of the IRepository interface.
     * @param repoClass The repository class.
     * @return The Class object representing the entity type, or {@code null} if it cannot be determined.
     */
    private Class<?> getRepositoryEntityType(Class<?> repoClass) {
        for (java.lang.reflect.Type type : repoClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == IRepository.class) {
                return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        return null;
    }
}
