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

    private final FileConfiguration base;

    private String title = "&cChange name color!";
    private String playerNotFound = "&c[Core] That player is not online!";
    private String onOpen = "&a[Core] Opened GUI for %player%.";
    private String noPermission = "&c[Core] You do not have permission to execute this command!";
    private String confirmName = "&a&lClick to confirm!";
    private List<String> confirmLore = Arrays.asList("&7Click to set", "&7your nickname", "&7to '&r%nickname%&7'.");
    private List<String> alias = new ArrayList<>();
    private List<String> colors = Arrays.asList("0:f", "1:6", "2:d", "3:b", "4:e", "5:a", "14:4", "6:c", "7:8", "8:7", "9:3", "10:5", "11:9", "13:2", "15:0");

    public Configuration(Core instance) {
        this.base = instance.getConfig();
    }

    protected void loadOptions() {
        if (base.isString("no-permission") && !base.getString("no-permission").isEmpty()) {
            noPermission = base.getString("no-permission");
        }

        if (base.isString("target-not-found") && !base.getString("target-not-found").isEmpty()) {
            playerNotFound = base.getString("target-not-found");
        }

        if (base.isString("on-open-others") && !base.getString("on-open-others").isEmpty()) {
            onOpen = base.getString("on-open-others");
        }

        if (base.isString("gui-items.confirinstance.name") && !base.getString("gui-items.confirinstance.name").isEmpty()) {
            confirmName = base.getString("gui-items.confirinstance.name");
        }

        if (base.isList("gui-items.confirinstance.lore") && !base.getStringList("gui-items.confirinstance.lore").isEmpty()) {
            confirmLore = base.getStringList("gui-items.confirinstance.lore");
        }

        if (base.isString("gui-title") && base.getString("gui-title").length() <= 32) {
            title = base.getString("gui-title");
        }

        if (base.isList("command-alias") && !base.getStringList("command-alias").isEmpty()) {
            alias = base.getStringList("command-alias");
        }

        if (base.isList("colors") && !base.getStringList("colors").isEmpty()) {
            colors = base.getStringList("colors");
        }
    }

    public String getTitle() {
        return title;
    }

    public String getPlayerNotFoundMessage() {
        return playerNotFound;
    }

    public String getOnOpenMessage() {
        return onOpen;
    }

    public String getNoPermissionMessage() {
        return noPermission;
    }

    public String getConfirmName() {
        return confirmName;
    }

    public List<String> getConfirmLore() {
        return confirmLore;
    }

    public List<String> getAlias() {
        return alias;
    }

    public List<String> getColors() {
        return colors;
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
