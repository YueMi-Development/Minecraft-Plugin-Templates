package org.yuemi.example.plugin.config;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages plugin configuration loading and versioned migrations.
 *
 * <p>Delegates to {@link org.yuemi.config.api.ConfigManager} from the
 * {@code mc-config-libs} library for auto-discovery and sequential application
 * of {@link org.yuemi.config.api.MigrationStep migration steps}.
 *
 * <p>To add a new config version, increment the {@code config-version} in your
 * default {@code config.yml} and add a corresponding migration class inside the
 * {@code org.yuemi.example.plugin.config.migrations} package that implements
 * {@link org.yuemi.config.api.MigrationStep}.
 */
public final class ConfigManager {

    private final org.yuemi.config.api.ConfigManager delegate;
    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.delegate = new org.yuemi.config.api.ConfigManager(
            plugin,
            "org.yuemi.example.plugin.config.migrations"
        );
    }

    /**
     * Loads the plugin configuration and applies any pending migrations.
     */
    public void loadAndMigrate() {
        delegate.loadAndMigrate(plugin);
    }
}
