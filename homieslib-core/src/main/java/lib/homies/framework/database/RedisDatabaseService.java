package lib.homies.framework.database;

import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public abstract class RedisDatabaseService implements DatabaseService {

    private static final Logger LOGGER = Logger.getLogger(RedisDatabaseService.class.getName());
    protected final String host;
    protected final int port;
    protected final String password;
    protected final int database;
    protected JedisPool jedisPool;

    public RedisDatabaseService(String host, int port, String password, int database) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.database = database;
    }

    public abstract boolean init();

    public abstract void onDisable();

    protected Jedis getResource() {
        if (jedisPool == null || jedisPool.isClosed()) {
            throw new IllegalStateException("JedisPool is not initialized or is closed.");
        }
        return jedisPool.getResource();
    }

    @Override
    public <T extends IRepository<?>> Optional<T> getRepository(Class<T> repoClass) {
        return Optional.empty();
    }

    @Override
    public <T> void saveEntity(T entity) {
        if (jedisPool == null) return;
        Class<?> entityClass = entity.getClass();
        DbEntity dbEntity = entityClass.getAnnotation(DbEntity.class);
        if (dbEntity == null) {
            throw new IllegalArgumentException("Class " + entityClass.getName() + " is not annotated with @DbEntity.");
        }

        String tableName = dbEntity.tableName().isEmpty() ? entity.getClass().getSimpleName() : dbEntity.tableName();
        String id = null;
        try (Jedis jedis = getResource()) {
            String key = null;
            for (Field field : entity.getClass().getDeclaredFields()) {
                DbField dbField = field.getAnnotation(DbField.class);
                if (dbField != null) {
                    field.setAccessible(true);
                    if (dbField.id()) {
                        Object idValue = field.get(entity);
                        if (idValue == null) {
                            throw new IllegalStateException("ID field for entity " + entityClass.getName() + " cannot be null during save.");
                        }
                        id = String.valueOf(idValue);
                        key = tableName + ":" + id;
                        break;
                    }
                }
            }

            if (id == null) {
                throw new IllegalStateException("Entity " + entity.getClass().getName() + " does not have an ID field annotated with @DbField(id=true).");
            }

            if (key == null) {
                throw new IllegalStateException("Could not construct Redis key for entity " + entity.getClass().getName());
            }

            Map<String, String> hash = new HashMap<>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                DbField dbField = field.getAnnotation(DbField.class);
                if (dbField != null) {
                    field.setAccessible(true);
                    String fieldName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                    Object value = field.get(entity);
                    if (value != null) {
                        hash.put(fieldName, value.toString());
                    }
                }
            }
            jedis.hset(key, hash);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to save entity due to reflection error.", e);
        }
    }

    @Override
    public <T> Optional<T> findById(Class<T> entityType, String id) {
        if (jedisPool == null) return Optional.empty();
        DbEntity dbEntity = entityType.getAnnotation(DbEntity.class);
        if (dbEntity == null) {
            throw new IllegalArgumentException("Class " + entityType.getName() + " is not annotated with @DbEntity.");
        }

        String tableName = dbEntity.tableName().isEmpty() ? entityType.getSimpleName() : dbEntity.tableName();
        String key = tableName + ":" + id;

        try (Jedis jedis = getResource()) {
            if (!jedis.exists(key)) {
                return Optional.empty();
            }

            T entity = entityType.getDeclaredConstructor().newInstance();
            Map<String, String> hash = jedis.hgetAll(key);

            for (Field field : entityType.getDeclaredFields()) {
                DbField dbField = field.getAnnotation(DbField.class);
                if (dbField != null) {
                    field.setAccessible(true);
                    String fieldName = dbField.columnName().isEmpty() ? field.getName() : dbField.columnName();
                    String value = hash.get(fieldName);
                    if (value != null) {
                        Object convertedValue = convertStringToObject(value, field.getType());
                        field.set(entity, convertedValue);
                    }
                }
            }
            return Optional.of(entity);
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to find or load entity by ID: " + key, e);
            return Optional.empty();
        }
    }

    private Object convertStringToObject(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == UUID.class) {
            return UUID.fromString(value);
        } else if (type == LocalDateTime.class) {
            return LocalDateTime.parse(value);
        } else if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, value);
        }
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityType) {
        if (jedisPool == null) return Collections.emptyList();
        DbEntity dbEntity = entityType.getAnnotation(DbEntity.class);
        if (dbEntity == null) {
            throw new IllegalArgumentException("Class " + entityType.getName() + " is not annotated with @DbEntity.");
        }

        String tableName = dbEntity.tableName().isEmpty() ? entityType.getSimpleName() : dbEntity.tableName();
        String pattern = tableName + ":*";

        List<T> entities = new ArrayList<>();
        try (Jedis jedis = getResource()) {
            Set<String> keys = jedis.keys(pattern);
            if (keys == null || keys.isEmpty()) {
                return Collections.emptyList();
            }

            for (String key : keys) {
                String id = key.substring(tableName.length() + 1);
                findById(entityType, id).ifPresent(entities::add);
            }
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to find all entities of type " + entityType.getName(), e);
        }
        return entities;
    }

    @Override
    public <T> void deleteEntity(T entity) {
        if (jedisPool == null) return;
        Class<?> entityClass = entity.getClass();
        DbEntity dbEntity = entityClass.getAnnotation(DbEntity.class);
        if (dbEntity == null) {
            throw new IllegalArgumentException("Class " + entityClass.getName() + " is not annotated with @DbEntity.");
        }

        String tableName = dbEntity.tableName().isEmpty() ? entity.getClass().getSimpleName() : dbEntity.tableName();
        String id = null;
        try (Jedis jedis = getResource()) {
            String key = null;
            for (Field field : entity.getClass().getDeclaredFields()) {
                DbField dbField = field.getAnnotation(DbField.class);
                if (dbField != null) {
                    field.setAccessible(true);
                    if (dbField.id()) {
                        Object idValue = field.get(entity);
                        if (idValue == null) {
                            throw new IllegalStateException("ID field for entity " + entityClass.getName() + " cannot be null during delete.");
                        }
                        id = String.valueOf(idValue);
                        key = tableName + ":" + id;
                        break;
                    }
                }
            }

            if (id == null) {
                throw new IllegalStateException("Entity " + entity.getClass().getName() + " does not have an ID field annotated with @DbField(id=true).");
            }

            if (key == null) {
                throw new IllegalStateException("Could not construct Redis key for entity " + entity.getClass().getName());
            }
            jedis.del(key);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to delete entity due to reflection error.", e);
        }
    }
}
