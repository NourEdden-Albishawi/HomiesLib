package lib.homies.framework.spigot.database;

import lib.homies.framework.database.DatabaseService;
import lib.homies.framework.database.IRepository;
import lib.homies.framework.spigot.HomiesLibSpigot;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class SpigotCachingDatabaseService implements DatabaseService {

    private final SpigotRedisDatabaseService redisService;
    private final SpigotMySQLDatabaseService mysqlService;

    public SpigotCachingDatabaseService(SpigotRedisDatabaseService redisService, SpigotMySQLDatabaseService mysqlService) {
        this.redisService = redisService;
        this.mysqlService = mysqlService;
    }

    public boolean init() {
        HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Initializing Caching Database Service...");
        boolean redisSuccess = redisService.init();
        if (!redisSuccess) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().severe("Redis service failed to initialize. Caching service will be disabled.");
            return false;
        }
        mysqlService.init(); // Initialize MySQL, but its success is not critical for the cache to function
        HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Caching Database Service initialized successfully.");
        return true;
    }

    public void onDisable() {
        HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Disabling Caching Database Service...");
        redisService.onDisable();
        mysqlService.onDisable();
        HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().info("Caching Database Service disabled.");
    }

    @Override
    public <T extends IRepository<?>> Optional<T> getRepository(Class<T> repoClass) {
        return Optional.empty();
    }

    @Override
    public <T> void saveEntity(T entity) {
        try {
            mysqlService.saveEntity(entity);
            redisService.saveEntity(entity);
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to save entity to caching service.", e);
        }
    }

    @Override
    public <T> Optional<T> findById(Class<T> entityType, String id) {
        try {
            Optional<T> entity = redisService.findById(entityType, id);
            if (entity.isPresent()) {
                return entity;
            }
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.WARNING, "Failed to retrieve from Redis. Falling back to MySQL.", e);
        }

        try {
            Optional<T> entity = mysqlService.findById(entityType, id);
            entity.ifPresent(redisService::saveEntity);
            return entity;
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to retrieve entity from MySQL.", e);
        }
        return Optional.empty();
    }

    @Override
    public <T> List<T> findByField(Class<T> entityType, String fieldName, Object value) {
        // For caching, complex queries like this usually bypass the cache and go to the source of truth.
        // The results are generally not cached to avoid complexity.
        try {
            return mysqlService.findByField(entityType, fieldName, value);
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to find entities by field from MySQL.", e);
            return Collections.emptyList();
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityType) {
        try {
            return mysqlService.findAll(entityType);
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to find all entities from MySQL.", e);
            return List.of();
        }
    }

    @Override
    public <T> void deleteEntity(T entity) {
        try {
            mysqlService.deleteEntity(entity);
            redisService.deleteEntity(entity);
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to delete entity from caching service.", e);
        }
    }

    @Override
    public <T> void deleteByField(Class<T> entityType, String fieldName, Object value) {
        try {
            mysqlService.deleteByField(entityType, fieldName, value);
            // Invalidate relevant cache entries if possible, or clear all for this entity type
            // For simplicity, we'll just delete from Redis based on the field, which might not be precise.
            // A more robust solution would involve fetching IDs from MySQL first, then deleting from Redis by ID.
            redisService.deleteByField(entityType, fieldName, value);
        } catch (Exception e) {
            HomiesLibSpigot.getPlugin(HomiesLibSpigot.class).getLogger().log(Level.SEVERE, "Failed to delete entities by field from caching service.", e);
        }
    }
}
