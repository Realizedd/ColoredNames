package me.realized.colorednames;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configuration {

    private ColoredNames m;
    private FileConfiguration c;
    private String guiTitle = "&cChange name color!";
    private String INVALID_TARGET = "&c[ColoredNames] That player is not online!";
    private String ON_OPEN = "&a[ColoredNames] Opened GUI for %player%.";
    private String NO_PERMISSION = "&c[ColoredNames] You do not have permission to execute this command!";
    private String confirmName = "&a&lClick to confirm!";
    private List<String> confirmLore = Arrays.asList("&7Click to set", "&7your nickname", "&7to '&r%nickname%&7'.");
    private List<String> alias = new ArrayList<>();
    private List<String> colors = Arrays.asList("0:f", "1:6", "2:d", "3:b", "4:e", "5:a", "14:4", "6:c", "7:8", "8:7", "9:3", "10:5", "11:9", "13:2", "15:0");

    public Configuration(ColoredNames m) {
        this.m = m;
        c = m.getConfig();
    }

    protected void loadOptions() {
        if (c.isString("no-permission") && !c.getString("no-permission").isEmpty()) {
            NO_PERMISSION = c.getString("no-permission");
            m.info("Loaded no permission message: '" + NO_PERMISSION + "'");
        }

        if (c.isString("target-not-found") && !c.getString("target-not-found").isEmpty()) {
            INVALID_TARGET = c.getString("target-not-found");
            m.info("Loaded invalid target message: '" + INVALID_TARGET + "'");
        }

        if (c.isString("on-open-others") && !c.getString("on-open-others").isEmpty()) {
            ON_OPEN = c.getString("on-open-others");
            m.info("Loaded on open others message: '" + ON_OPEN + "'");
        }

        if (c.isString("gui-items.confirm.name") && !c.getString("gui-items.confirm.name").isEmpty()) {
            confirmName = c.getString("gui-items.confirm.name");
            m.info("Loaded confirm item's display name: '" + confirmName + "'");
        }

        if (c.isList("gui-items.confirm.lore") && !c.getStringList("gui-items.confirm.lore").isEmpty()) {
            confirmLore = c.getStringList("gui-items.confirm.lore");
            m.info("Loaded confirm item's lore: " + confirmLore.toString());
        }

        if (c.isString("gui-title") && c.getString("gui-title").length() <= 32) {
            guiTitle = c.getString("gui-title");
            m.info("Loaded GUI Title: '" + guiTitle + "'");
        } else {
            m.warn("Failed to load GUI Title from the config, using the default one for now: title length must be 32 or less");
        }

        if (c.isList("command-alias") && !c.getStringList("command-alias").isEmpty()) {
            alias = c.getStringList("command-alias");
            m.info("Loaded command alias: " + alias.toString());
        }

        if (c.isList("colors") && !c.getStringList("colors").isEmpty()) {
            colors = c.getStringList("colors");
            m.info("Loaded colors: " + colors.toString());
        } else {
            m.warn("Failed to load colors from the config, using the default one for now: list is either empty or invalid");
        }
    }

    protected String getTitle() {
        return guiTitle;
    }

    protected List<String> getAlias() {
        return alias;
    }

    protected String getNPMessage() {
        return NO_PERMISSION;
    }

    protected String getITMessage() {
        return INVALID_TARGET;
    }

    protected String getOOMessage() {
        return ON_OPEN;
    }

    protected String getConfirmName() {
        return confirmName;
    }

    protected List<String> getConfirmLore() {
        return confirmLore;
    }

    protected String get(short data) {
        for (String s : colors) {
            if (Short.valueOf(s.split(":")[0]) == data) {
                return s;
            }
        }
        return null;
    }

    protected String getColor(short data) {
        for (String s : colors) {
            if (Short.valueOf(s.split(":")[0]) == data) {
                return s.split(":")[1];
            }
        }
        return null;
    }

    protected short getFirst() {
        return Short.valueOf(colors.get(0).split(":")[0]);
    }

    private short getFinal() {
        return Short.valueOf(colors.get(colors.size() - 1).split(":")[0]);
    }

    private String c(String txt) {
        return ChatColor.translateAlternateColorCodes('&', txt);
    }

    protected short getNext(short data) {
        if (colors.size() == 1) {
            return getFirst();
        }

        boolean isFinal = colors.indexOf(get(data)) == colors.size() - 1;

        if (isFinal) {
            return getFirst();
        }

        return Short.valueOf(colors.get(colors.indexOf(get(data)) + 1).split(":")[0]);
    }

    protected short getBefore(short data) {
        if (colors.size() == 1) {
            return getFirst();
        }

        boolean isStart = colors.indexOf(get(data)) == 0;

        if (isStart) {
            return getFinal();
        }

        return Short.valueOf(colors.get(colors.indexOf(get(data)) - 1).split(":")[0]);
    }

    protected StringBuilder getNickname(Inventory gui, String name) {
        StringBuilder nickname = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            ItemStack item = gui.getItem(i);

            if (item != null) {
                String displayName = item.getItemMeta().getDisplayName();
                String color = "&r";

                if (getColor(item.getDurability()) != null) {
                    color = getColor(item.getDurability());
                }

                nickname.append(color).append(displayName.substring(displayName.length() - 1)).append("&r");
            }
        }

        return nickname;
    }

    protected void update(ItemStack item, String nickname) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(c(getConfirmName().replace("%nickname%", nickname)));
        }

        if (meta.hasLore()) {
            List<String> updated = new ArrayList<>();
            for (String s : getConfirmLore()) {
                updated.add(c(s.replace("%nickname%", nickname)));
            }

            meta.setLore(updated);
        }

        item.setItemMeta(meta);
    }
}
