package org.yuemi.example.plugin.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Represents a single migration step from one configuration version to the next.
 */
public interface ConfigMigration {

    /**
     * Performs the migration on the configuration.
     *
     * @param config the configuration instance to modify
     */
    void migrate(FileConfiguration config);
}
