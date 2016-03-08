package me.realized.colorednames;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Core extends JavaPlugin {

    private Configuration config;

    @Override
    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", true);
            info("config.yml was not found! generating default configuration.");
        }

        config = new Configuration(this);
        config.loadOptions();

        getCommand("color").setExecutor(new ColorCommand(this));
        manager.registerEvents(new ColorListener(this), this);
    }

    public Configuration getConfiguration() {
        return config;
    }

    public void info(String txt) {
        getLogger().info(txt);
    }

    public String color(String txt) {
        return ChatColor.translateAlternateColorCodes('&', txt);
    }

    public void pm(CommandSender sender, String msg) {
        sender.sendMessage(color(msg));
    }
}
