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

public class GearListener implements Listener {

    private final InvisibleGear plugin;

    public GearListener(InvisibleGear plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getGearManager().updateForViewer(player);
        }, 10L);

        if (plugin.getConfig().getBoolean("persistence.restore-on-join", true)) {
            if (plugin.getGearManager().isGearHidden(player.getUniqueId())) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getGearManager().updateGearVisibility(player);
                    player.sendMessage(plugin.getMessage("status-hidden"));
                }, 20L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getGearManager().handleDeath(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getGearManager().handleRespawn(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        if (!plugin.getGearManager().isGearHidden(player))
            return;

        int slot = event.getRawSlot();
        if (slot >= 5 && slot <= 8) { // Armor slots
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }

        if (slot == 45) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGearManager().isGearHidden(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getGearManager().updateGearVisibility(player);
            }, 2L);
        }
    }

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
                plugin.getGearManager().restoreGearVisibility(player);
            }
        }
    }
}
