package com.github.tejaslamba2006.invisiblegear.listeners;

import com.github.tejaslamba2006.invisiblegear.InvisibleGear;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

/**
 * Handles events related to gear visibility
 */
public class GearListener implements Listener {

    private final InvisibleGear plugin;

    public GearListener(InvisibleGear plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle player join - restore hidden state if configured
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Update visibility for this player viewing others
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getGearManager().updateForViewer(player);
        }, 10L);

        // Restore hidden state if configured
        if (plugin.getConfig().getBoolean("persistence.restore-on-join", true)) {
            if (plugin.getGearManager().isGearHidden(player.getUniqueId())) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getGearManager().updateGearVisibility(player);
                    player.sendMessage(plugin.getMessage("status-hidden"));
                }, 20L);
            }
        }
    }

    /**
     * Handle player quit - save state if configured
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Data is automatically saved by DataManager when state changes
    }

    /**
     * Handle player death
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getGearManager().handleDeath(player);
    }

    /**
     * Handle player respawn
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getGearManager().handleRespawn(player);
    }

    /**
     * Handle item held change - update visibility
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            // Slight delay to ensure the change is processed
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    /**
     * Handle inventory click - update visibility when armor changes
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        if (!plugin.getGearManager().isGearHidden(player))
            return;

        // Check if armor slot was clicked
        int slot = event.getRawSlot();
        if (slot >= 5 && slot <= 8) { // Armor slots
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }

        // Also check if it's the offhand slot (45 in player inventory)
        if (slot == 45) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    /**
     * Handle player swap hands
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    /**
     * Handle item drop - update visibility
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    /**
     * Handle item pickup - update visibility
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 2L);
        }
    }

    /**
     * Handle world change - check if enabled in new world
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            boolean enabled = plugin.getConfig().getBoolean("worlds." + player.getWorld().getName(),
                    plugin.getConfig().getBoolean("worlds.default", true));

            if (enabled) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getGearManager().updateGearVisibility(player);
                }, 5L);
            } else {
                // Show gear in disabled world
                plugin.getGearManager().restoreGearVisibility(player);
            }
        }
    }
}
