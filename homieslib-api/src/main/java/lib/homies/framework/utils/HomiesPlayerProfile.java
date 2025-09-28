package lib.homies.framework.utils;

/**
 * A platform-agnostic representation of a player profile.
 * This interface abstracts away platform-specific player profile objects (e.g., Paper's PlayerProfile).
 * It is primarily used in conjunction with {@link ItemBuilder} for creating player heads.
 * The concrete implementation will be provided by a platform-specific module,
 * which can be unwrapped to get the underlying platform object if needed.
 */
public interface HomiesPlayerProfile {
}
