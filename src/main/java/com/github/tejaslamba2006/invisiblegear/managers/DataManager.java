package com.github.tejaslamba2006.invisiblegear.managers;

import com.github.tejaslamba2006.invisiblegear.InvisibleGear;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Manages persistent data storage for invisible gear states
 */
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
    
    /**
     * Load data from file
     */
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
            
            // Load hidden players
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
    
    /**
     * Save all data to file
     */
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
    
    /**
     * Check if a player has hidden gear
     */
    public boolean isHidden(UUID playerId) {
        return hiddenPlayers.contains(playerId);
    }
    
    /**
     * Set a player's hidden state
     */
    public void setHidden(UUID playerId, boolean hidden) {
        if (hidden) {
            hiddenPlayers.add(playerId);
        } else {
            hiddenPlayers.remove(playerId);
        }
        
        // Auto-save if using file storage
        if (plugin.getConfig().getString("persistence.storage", "FILE").equalsIgnoreCase("FILE")) {
            saveAll();
        }
    }
    
    /**
     * Get all players with hidden gear
     */
    public Set<UUID> getHiddenPlayers() {
        return new HashSet<>(hiddenPlayers);
    }
    
    /**
     * Clear all data
     */
    public void clearAll() {
        hiddenPlayers.clear();
        saveAll();
    }
}
