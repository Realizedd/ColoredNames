package me.realized.colorednames;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ColorCommand implements CommandExecutor {

    private final Core instance;
    private final Configuration config;

    public ColorCommand(Core instance) {
        this.instance = instance;
        this.config = instance.getConfiguration();
    }

    private void openGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, instance.color(config.getTitle()));
        String name = player.getName();
        for (int i = 0; i < name.length(); i++) {
            ItemStack item = new ItemStack(Material.WOOL, 1, config.getFirst());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(instance.color(config.get(config.getFirst()).split(":")[1] + "&l" + String.valueOf(name.charAt(i))));
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }

        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(instance.color(config.getConfirmName().replace("%nickname%", player.getName())));

        if (!config.getConfirmLore().isEmpty()) {
            List<String> colorized = new ArrayList<>();
            for (String s : config.getConfirmLore()) {
                colorized.add(instance.color(s.replace("%nickname%", player.getName())));
            }
            confirmMeta.setLore(colorized);
        }

        confirm.setItemMeta(confirmMeta);
        inventory.setItem(31, confirm);
        player.openInventory(inventory);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            instance.pm(sender, "&cNo Console support.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("colorednames.use")) {
            instance.pm(player, config.getNoPermissionMessage());
            return true;
        }

        if (args.length == 0) {
            openGUI(player);
            return true;
        }

        if (!player.hasPermission("colorednames.use.others")) {
            instance.pm(player, config.getNoPermissionMessage());
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            instance.pm(player, config.getPlayerNotFoundMessage());
            return true;
        }

        openGUI(target);
        instance.pm(player, config.getOnOpenMessage().replace("%player%", target.getName()));
        return true;
    }
}
