package me.realized.colorednames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ColorListener implements Listener {

    private Configuration config;

    public ColorListener(ColoredNames m) {
        config = m.get();
    }

    private String color(String txt) {
        return ChatColor.translateAlternateColorCodes('&', txt);
    }

    private void pm(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    private void updateStack(Inventory inventory, int slot, ClickType click, String name) {
        ItemStack item = inventory.getItem(slot);
        String displayName = item.getItemMeta().getDisplayName();
        short type = item.getDurability();
        short next = 0;

        switch (click) {
            case LEFT:
                next = config.getBefore(type);
                break;
            case RIGHT:
                next = config.getNext(type);
                break;
            default:
                next = type;
                break;
        }

        ItemStack updated = new ItemStack(Material.WOOL, 1, next);
        ItemMeta meta = updated.getItemMeta();
        meta.setDisplayName(color(config.get(next).split(":")[1] + displayName.substring(displayName.length() - 1)));
        updated.setItemMeta(meta);
        inventory.setItem(slot, updated);
        config.update(inventory.getItem(31), config.getNickname(inventory, name).toString());
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        Inventory top = p.getOpenInventory().getTopInventory();

        if (inv == null || top == null) {
            return;
        }

        ItemStack item = e.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (top.getTitle().equals(color(config.getTitle()))) {
            e.setCancelled(true);

            switch (item.getType()) {
                case REDSTONE_BLOCK:
                    p.closeInventory();
                    break;
                case EMERALD_BLOCK:
                    ItemStack confirmed = new ItemStack(Material.REDSTONE_BLOCK, 1);
                    top.setItem(31, confirmed);
                    p.closeInventory();
                    break;
                case WOOL:
                    updateStack(top, e.getSlot(), e.getClick(), p.getName());
            }
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();

        if (!inv.getTitle().equals(color(config.getTitle()))) {
            return;
        }

        if (inv.getItem(31).getType() != Material.REDSTONE_BLOCK) {
            return;
        }

        StringBuilder nickname = config.getNickname(inv, p.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nick " + p.getName() + " " + nickname.toString());
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent e) {
        if (config.getAlias().isEmpty()) {
            return;
        }

        String command = e.getMessage().substring(1).split(" ")[0];

        if (config.getAlias().contains(command.toLowerCase())) {
            e.getPlayer().performCommand("color");
            e.setCancelled(true);
        }
    }
}
