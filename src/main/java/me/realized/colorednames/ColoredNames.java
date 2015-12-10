package me.realized.colorednames;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ColoredNames extends JavaPlugin {

    private Configuration config;

    @Override
    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();
        if (manager.getPlugin("Essentials") == null) {
            warn("This plugin requires Essentials, but it wasn't loaded on your server.");
            warn("Disabling.");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", true);
            info("config.yml was not found, generating default configuration.");
        }

        config = new Configuration(this);
        config.loadOptions();

        this.getCommand("color").setExecutor(new ColorCommand(this));
        manager.registerEvents(new ColorListener(this), this);
    }

    protected Configuration get() {
        return config;
    }

    protected void info(String txt) {
        getLogger().info(txt);
    }

    protected void warn(String txt) {
        getLogger().warning(txt);
    }
}
