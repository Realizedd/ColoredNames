package me.realized.colorednames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private Configuration config;

    public ColorCommand(ColoredNames m) {
        config = m.get();
    }

    private String color(String txt) {
        return ChatColor.translateAlternateColorCodes('&', txt);
    }

    private void pm(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    private void openGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, color(config.getTitle()));
        String name = player.getName();
        for (int i = 0; i < name.length(); i++) {
            ItemStack item = new ItemStack(Material.WOOL, 1, config.getFirst());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(color(config.get(config.getFirst()).split(":")[1] + "&l" + String.valueOf(name.charAt(i))));
            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }

        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(color(config.getConfirmName().replace("%nickname%", player.getName())));

        if (!config.getConfirmLore().isEmpty()) {
            List<String> colorized = new ArrayList<>();
            for (String s : config.getConfirmLore()) {
                colorized.add(color(s.replace("%nickname%", player.getName())));
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
            pm(sender, "&cCommand '" + command.getName() + "' cannot be executed from CONSOLE");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("colorednames.use")) {
            pm(player, config.getNPMessage());
            return true;
        }

        if (args.length == 0) {
            openGUI(player);
            return true;
        }

        if (args.length > 0) {
            if (!player.hasPermission("colorednames.use.others")) {
                pm(player, config.getNPMessage());
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                pm(player, config.getITMessage());
                return true;
            }

            openGUI(target);
            pm(player, config.getOOMessage().replace("%player%", target.getName()));
        }

        return true;
    }
}
