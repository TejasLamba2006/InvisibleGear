package com.github.tejaslamba2006.invisiblegear;

import com.github.tejaslamba2006.invisiblegear.commands.InvisibleGearCommand;
import com.github.tejaslamba2006.invisiblegear.commands.VanishGearCommand;
import com.github.tejaslamba2006.invisiblegear.listeners.GearListener;
import com.github.tejaslamba2006.invisiblegear.managers.GearManager;
import com.github.tejaslamba2006.invisiblegear.managers.DataManager;
import com.github.tejaslamba2006.invisiblegear.utils.PluginAdvertiser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public class InvisibleGear extends JavaPlugin {
    
    private static InvisibleGear instance;
    private GearManager gearManager;
    private DataManager dataManager;
    private MiniMessage miniMessage;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize MiniMessage
        miniMessage = MiniMessage.miniMessage();
        
        // Initialize managers
        dataManager = new DataManager(this);
        gearManager = new GearManager(this);
        
        // Register commands
        InvisibleGearCommand mainCommand = new InvisibleGearCommand(this);
        getCommand("invisiblegear").setExecutor(mainCommand);
        getCommand("invisiblegear").setTabCompleter(mainCommand);
        
        VanishGearCommand vanishCommand = new VanishGearCommand(this);
        getCommand("vanishgear").setExecutor(vanishCommand);
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new GearListener(this), this);
        
        // Show advertisement
        if (getConfig().getBoolean("show-advertisement", true)) {
            PluginAdvertiser.showEnableMessage(this);
        }
        
        getLogger().info("InvisibleGear enabled! Hide your gear from enemies!");
    }
    
    @Override
    public void onDisable() {
        // Save data
        if (dataManager != null) {
            dataManager.saveAll();
        }
        
        // Clean up gear manager
        if (gearManager != null) {
            gearManager.showAllGear();
        }
        
        getLogger().info("InvisibleGear disabled!");
    }
    
    /**
     * Get plugin instance
     */
    public static InvisibleGear getInstance() {
        return instance;
    }
    
    /**
     * Get gear manager
     */
    public GearManager getGearManager() {
        return gearManager;
    }
    
    /**
     * Get data manager
     */
    public DataManager getDataManager() {
        return dataManager;
    }
    
    /**
     * Reload plugin
     */
    public void reload() {
        reloadConfig();
        gearManager.reload();
    }
    
    /**
     * Colorize text with legacy color codes and MiniMessage
     */
    public Component colorize(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        
        // First convert legacy color codes to section symbols
        String legacyText = text.replace('&', '§');
        
        // Check if text contains MiniMessage tags
        if (text.contains("<") && text.contains(">")) {
            return miniMessage.deserialize(text);
        }
        
        return LegacyComponentSerializer.legacySection().deserialize(legacyText);
    }
    
    /**
     * Get a message from config with prefix
     */
    public Component getMessage(String key, String... replacements) {
        String prefix = getConfig().getString("messages.prefix", "&8[&bInvisibleGear&8] &7");
        String message = getConfig().getString("messages." + key, key);
        
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        
        return colorize(prefix + message);
    }
    
    /**
     * Get a raw message without prefix
     */
    public Component getRawMessage(String key, String... replacements) {
        String message = getConfig().getString("messages." + key, key);
        
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        
        return colorize(message);
    }
}
