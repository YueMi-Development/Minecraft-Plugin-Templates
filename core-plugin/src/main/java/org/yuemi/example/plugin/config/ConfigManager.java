package org.yuemi.example.plugin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages plugin configuration loading and versioned migrations.
 *
 * <p>On each call to {@link #loadAndMigrate()}, this manager:
 * <ol>
 *   <li>Saves the default {@code config.yml} from the JAR if it doesn't exist.</li>
 *   <li>Reads the {@code config-version} field from the current config file.</li>
 *   <li>Reflectively discovers and applies every {@code MigrationVNToV(N+1)} class
 *       in the {@code org.yuemi.example.config.migrations} package, in order, until
 *       no further migration class is found.</li>
 *   <li>Persists the updated config if any migrations were applied.</li>
 * </ol>
 *
 * <p>To add a new config version, increment {@link #LATEST_VERSION} and add a
 * corresponding {@code MigrationVNToV(N+1)} class inside the {@code migrations}
 * sub-package that implements {@link ConfigMigration}.
 */
public final class ConfigManager {

    private final JavaPlugin plugin;

    /**
     * The highest known config version. Increment this whenever a new migration is added.
     */
    private static final int LATEST_VERSION = 1;

    /**
     * Fully-qualified package where migration classes live.
     * Migration class naming convention: {@code MigrationV<from>ToV<to>}.
     */
    private static final String MIGRATION_PACKAGE = "org.yuemi.example.config.migrations";

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads the plugin configuration and applies any pending migrations.
     */
    public void loadAndMigrate() {
        // Copy default config.yml from jar if it doesn't exist
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();
        int currentVersion = config.getInt("config-version", 1);
        int migratedVersion = currentVersion;

        plugin.getLogger().info("Current configuration version: " + currentVersion + " (Latest: " + LATEST_VERSION + ")");

        while (true) {
            int nextVersion = migratedVersion + 1;
            String migrationClassName = MIGRATION_PACKAGE + ".MigrationV" + migratedVersion + "ToV" + nextVersion;

            try {
                Class<?> clazz = Class.forName(migrationClassName);
                if (ConfigMigration.class.isAssignableFrom(clazz)) {
                    ConfigMigration migration = (ConfigMigration) clazz.getDeclaredConstructor().newInstance();
                    plugin.getLogger().info("Applying configuration migration from version " + migratedVersion + " to " + nextVersion + "...");
                    migration.migrate(config);
                    migratedVersion = nextVersion;
                    config.set("config-version", migratedVersion);
                } else {
                    plugin.getLogger().warning("Class " + migrationClassName + " does not implement ConfigMigration.");
                    break;
                }
            } catch (ClassNotFoundException e) {
                // No more migrations found — we are up-to-date
                break;
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to apply configuration migration " + migrationClassName + ": " + e.getMessage());
                break;
            }
        }

        if (migratedVersion > currentVersion) {
            plugin.saveConfig();
            plugin.getLogger().info("Configuration successfully migrated to version " + migratedVersion + ".");
        } else {
            plugin.getLogger().info("Configuration is up to date.");
        }

        if (migratedVersion != LATEST_VERSION) {
            plugin.getLogger().warning(
                "Configuration version mismatch! Migrated version: " + migratedVersion
                + ", Expected latest version: " + LATEST_VERSION
            );
        }
    }
}
