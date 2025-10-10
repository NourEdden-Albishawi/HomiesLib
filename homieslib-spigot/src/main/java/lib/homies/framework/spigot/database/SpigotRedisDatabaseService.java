package lib.homies.framework.spigot.database;

import lib.homies.framework.database.RedisDatabaseService;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SpigotRedisDatabaseService extends RedisDatabaseService {

    public SpigotRedisDatabaseService(String host, int port, String password, int database) {
        super(host, port, password, database);
    }

    @Override
    public boolean init() {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(128);
            poolConfig.setMaxIdle(128);
            poolConfig.setMinIdle(16);

            if (password != null && !password.isEmpty()) {
                this.jedisPool = new JedisPool(poolConfig, host, port, 2000, password, database);
            } else {
                this.jedisPool = new JedisPool(poolConfig, host, port, 2000, null, database);
            }

            try (Jedis jedis = getResource()) {
                jedis.ping();
            }
            Bukkit.getLogger().info("Successfully connected to Redis server.");
            return true;
        } catch (JedisConnectionException e) {
            Bukkit.getLogger().warning(
                    "Failed to connect to Redis at '" + host + ":" + port + "'. " +
                            "Please check your configuration and ensure the server is running. Redis service will be disabled."
            );
            this.jedisPool = null;
            return false;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "An unexpected error occurred while initializing the Redis connection pool.", e);
            this.jedisPool = null;
            return false;
        }
    }

    @Override
    public void onDisable() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            Bukkit.getLogger().info("Redis connection pool closed.");
        }
    }

    @Override
    public <T> List<T> findByField(Class<T> entityType, String fieldName, Object value) {
        return findAll(entityType).stream()
                .filter(entity -> {
                    try {
                        Field field = entity.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object fieldValue = field.get(entity);
                        return Objects.equals(fieldValue, value);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public <T> void deleteByField(Class<T> entityType, String fieldName, Object value) {
        if (jedisPool == null) return;
        // This is inefficient in Redis and should be used with caution.
        // It requires fetching all entities, filtering them, and then deleting them one by one.
        List<T> entitiesToDelete = findByField(entityType, fieldName, value);
        for (T entity : entitiesToDelete) {
            deleteEntity(entity);
        }
    }
}
