package com.github.tejaslamba2006.invisiblegear.managers;

import com.github.tejaslamba2006.invisiblegear.InvisibleGear;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataManager {

    private final InvisibleGear plugin;
    private final Set<UUID> hiddenPlayers;
    private File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(InvisibleGear plugin) {
        this.plugin = plugin;
        this.hiddenPlayers = new HashSet<>();

        loadData();
    }

    private void loadData() {
        String storage = plugin.getConfig().getString("persistence.storage", "FILE");

        if (storage.equalsIgnoreCase("FILE")) {
            dataFile = new File(plugin.getDataFolder(), "invisiblegear_data.yml");

            if (!dataFile.exists()) {
                try {
                    dataFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().warning("Could not create data file: " + e.getMessage());
                }
            }

            dataConfig = YamlConfiguration.loadConfiguration(dataFile);

            if (dataConfig.contains("hidden-players")) {
                for (String uuidStr : dataConfig.getStringList("hidden-players")) {
                    try {
                        hiddenPlayers.add(UUID.fromString(uuidStr));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid UUID in data file: " + uuidStr);
                    }
                }
            }

            plugin.getLogger().info("Loaded " + hiddenPlayers.size() + " players with hidden gear.");
        }
    }

    public void saveAll() {
        String storage = plugin.getConfig().getString("persistence.storage", "FILE");

        if (storage.equalsIgnoreCase("FILE") && dataConfig != null && dataFile != null) {
            dataConfig.set("hidden-players", hiddenPlayers.stream()
                    .map(UUID::toString)
                    .toList());

            try {
                dataConfig.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save data file: " + e.getMessage());
            }
        }
    }

    public boolean isHidden(UUID playerId) {
        return hiddenPlayers.contains(playerId);
    }

    public void setHidden(UUID playerId, boolean hidden) {
        if (hidden) {
            hiddenPlayers.add(playerId);
        } else {
            hiddenPlayers.remove(playerId);
        }

        if (plugin.getConfig().getString("persistence.storage", "FILE").equalsIgnoreCase("FILE")) {
            saveAll();
        }
    }

    public Set<UUID> getHiddenPlayers() {
        return new HashSet<>(hiddenPlayers);
    }

    public void clearAll() {
        hiddenPlayers.clear();
        saveAll();
    }
}
