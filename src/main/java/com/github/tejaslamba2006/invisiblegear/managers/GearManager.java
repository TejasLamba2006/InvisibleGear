package com.github.tejaslamba2006.invisiblegear.managers;

import com.github.tejaslamba2006.invisiblegear.InvisibleGear;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GearManager {

    private final InvisibleGear plugin;
    private final Set<UUID> hiddenGearPlayers;
    private final Map<UUID, Long> cooldowns;
    private BukkitTask refreshTask;

    private final Map<UUID, ItemStack[]> cachedEquipment;

    public GearManager(InvisibleGear plugin) {
        this.plugin = plugin;
        this.hiddenGearPlayers = new HashSet<>();
        this.cooldowns = new HashMap<>();
        this.cachedEquipment = new HashMap<>();

        hiddenGearPlayers.addAll(plugin.getDataManager().getHiddenPlayers());

        startRefreshTask();
    }

    public boolean toggleGear(Player player) {
        if (isGearHidden(player)) {
            showGear(player);
            return false;
        } else {
            hideGear(player);
            return true;
        }
    }

    public void hideGear(Player player) {
        UUID playerId = player.getUniqueId();

        if (!isWorldEnabled(player.getWorld())) {
            player.sendMessage(plugin.getMessage("world-disabled"));
            return;
        }

        if (isOnCooldown(playerId)) {
            long remaining = getCooldownRemaining(playerId);
            player.sendMessage(plugin.getMessage("cooldown", "{time}", String.valueOf(remaining)));
            return;
        }

        if (hiddenGearPlayers.contains(playerId)) {
            return; // Already hidden
        }

        hiddenGearPlayers.add(playerId);
        plugin.getDataManager().setHidden(playerId, true);

        cacheEquipment(player);

        updateGearVisibility(player);

        applyCooldown(playerId);

        playHideEffects(player);

        player.sendMessage(plugin.getMessage("gear-hidden"));
    }

    public void showGear(Player player) {
        UUID playerId = player.getUniqueId();

        if (isOnCooldown(playerId)) {
            long remaining = getCooldownRemaining(playerId);
            player.sendMessage(plugin.getMessage("cooldown", "{time}", String.valueOf(remaining)));
            return;
        }

        if (!hiddenGearPlayers.contains(playerId)) {
            return; // Already visible
        }

        hiddenGearPlayers.remove(playerId);
        plugin.getDataManager().setHidden(playerId, false);

        cachedEquipment.remove(playerId);

        restoreGearVisibility(player);

        applyCooldown(playerId);

        playShowEffects(player);

        player.sendMessage(plugin.getMessage("gear-shown"));
    }

    public void showAllGear() {
        for (UUID playerId : new HashSet<>(hiddenGearPlayers)) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                restoreGearVisibility(player);
            }
        }
        hiddenGearPlayers.clear();
    }

    public boolean isGearHidden(Player player) {
        return hiddenGearPlayers.contains(player.getUniqueId());
    }

    public boolean isGearHidden(UUID playerId) {
        return hiddenGearPlayers.contains(playerId);
    }

    public void updateGearVisibility(Player hiddenPlayer) {
        if (!isGearHidden(hiddenPlayer))
            return;

        boolean hideArmor = plugin.getConfig().getBoolean("hide.armor", true);
        boolean hideMainHand = plugin.getConfig().getBoolean("hide.main-hand", true);
        boolean hideOffHand = plugin.getConfig().getBoolean("hide.off-hand", true);

        Map<org.bukkit.inventory.EquipmentSlot, ItemStack> emptyEquipment = new EnumMap<>(
                org.bukkit.inventory.EquipmentSlot.class);

        if (hideArmor) {
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.HEAD, new ItemStack(Material.AIR));
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.CHEST, new ItemStack(Material.AIR));
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.LEGS, new ItemStack(Material.AIR));
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.FEET, new ItemStack(Material.AIR));
        }

        if (hideMainHand) {
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.HAND, new ItemStack(Material.AIR));
        }

        if (hideOffHand) {
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.OFF_HAND, new ItemStack(Material.AIR));
        }

        int maxDistance = plugin.getConfig().getInt("update.max-distance", 0);

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.equals(hiddenPlayer))
                continue;

            if (viewer.hasPermission("invisiblegear.bypass"))
                continue;

            if (maxDistance > 0 && !viewer.getWorld().equals(hiddenPlayer.getWorld()))
                continue;
            if (maxDistance > 0 && viewer.getLocation().distance(hiddenPlayer.getLocation()) > maxDistance)
                continue;

            viewer.sendEquipmentChange(hiddenPlayer, emptyEquipment);
        }
    }

    public void restoreGearVisibility(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null)
            return;

        Map<org.bukkit.inventory.EquipmentSlot, ItemStack> realEquipment = new EnumMap<>(
                org.bukkit.inventory.EquipmentSlot.class);
        realEquipment.put(org.bukkit.inventory.EquipmentSlot.HEAD, equipment.getHelmet());
        realEquipment.put(org.bukkit.inventory.EquipmentSlot.CHEST, equipment.getChestplate());
        realEquipment.put(org.bukkit.inventory.EquipmentSlot.LEGS, equipment.getLeggings());
        realEquipment.put(org.bukkit.inventory.EquipmentSlot.FEET, equipment.getBoots());
        realEquipment.put(org.bukkit.inventory.EquipmentSlot.HAND, equipment.getItemInMainHand());
        realEquipment.put(org.bukkit.inventory.EquipmentSlot.OFF_HAND, equipment.getItemInOffHand());

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.equals(player))
                continue;
            viewer.sendEquipmentChange(player, realEquipment);
        }
    }

    public void updateForViewer(Player viewer) {
        for (UUID hiddenId : hiddenGearPlayers) {
            Player hiddenPlayer = Bukkit.getPlayer(hiddenId);
            if (hiddenPlayer != null && hiddenPlayer.isOnline() && !viewer.equals(hiddenPlayer)) {
                if (!viewer.hasPermission("invisiblegear.bypass")) {
                    updateGearVisibilityForViewer(hiddenPlayer, viewer);
                }
            }
        }
    }

    private void updateGearVisibilityForViewer(Player hiddenPlayer, Player viewer) {
        boolean hideArmor = plugin.getConfig().getBoolean("hide.armor", true);
        boolean hideMainHand = plugin.getConfig().getBoolean("hide.main-hand", true);
        boolean hideOffHand = plugin.getConfig().getBoolean("hide.off-hand", true);

        Map<org.bukkit.inventory.EquipmentSlot, ItemStack> emptyEquipment = new EnumMap<>(
                org.bukkit.inventory.EquipmentSlot.class);

        if (hideArmor) {
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.HEAD, new ItemStack(Material.AIR));
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.CHEST, new ItemStack(Material.AIR));
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.LEGS, new ItemStack(Material.AIR));
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.FEET, new ItemStack(Material.AIR));
        }

        if (hideMainHand) {
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.HAND, new ItemStack(Material.AIR));
        }

        if (hideOffHand) {
            emptyEquipment.put(org.bukkit.inventory.EquipmentSlot.OFF_HAND, new ItemStack(Material.AIR));
        }

        viewer.sendEquipmentChange(hiddenPlayer, emptyEquipment);
    }

    private void cacheEquipment(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null)
            return;

        ItemStack[] cached = new ItemStack[6];
        cached[0] = equipment.getHelmet() != null ? equipment.getHelmet().clone() : null;
        cached[1] = equipment.getChestplate() != null ? equipment.getChestplate().clone() : null;
        cached[2] = equipment.getLeggings() != null ? equipment.getLeggings().clone() : null;
        cached[3] = equipment.getBoots() != null ? equipment.getBoots().clone() : null;
        cached[4] = equipment.getItemInMainHand().clone();
        cached[5] = equipment.getItemInOffHand().clone();

        cachedEquipment.put(player.getUniqueId(), cached);
    }

    private void startRefreshTask() {
        int interval = plugin.getConfig().getInt("update.refresh-interval", 10);

        if (interval <= 0)
            return;

        refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID playerId : hiddenGearPlayers) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    updateGearVisibility(player);
                }
            }
        }, interval, interval);
    }

    private boolean isWorldEnabled(World world) {
        boolean defaultEnabled = plugin.getConfig().getBoolean("worlds.default", true);
        return plugin.getConfig().getBoolean("worlds." + world.getName(), defaultEnabled);
    }

    private boolean isOnCooldown(UUID playerId) {
        if (!plugin.getConfig().getBoolean("cooldown.enabled", false))
            return false;

        Long cooldownEnd = cooldowns.get(playerId);
        if (cooldownEnd == null)
            return false;

        return System.currentTimeMillis() < cooldownEnd;
    }

    private long getCooldownRemaining(UUID playerId) {
        Long cooldownEnd = cooldowns.get(playerId);
        if (cooldownEnd == null)
            return 0;

        long remaining = cooldownEnd - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    private void applyCooldown(UUID playerId) {
        if (!plugin.getConfig().getBoolean("cooldown.enabled", false))
            return;

        Player player = Bukkit.getPlayer(playerId);
        if (player != null && player.hasPermission("invisiblegear.cooldown.bypass"))
            return;

        int duration = plugin.getConfig().getInt("cooldown.duration", 5);
        cooldowns.put(playerId, System.currentTimeMillis() + (duration * 1000L));
    }

    private void playHideEffects(Player player) {
        if (plugin.getConfig().getBoolean("visual.particles.enabled", true)) {
            String particleType = plugin.getConfig().getString("visual.particles.type", "CLOUD");
            int count = plugin.getConfig().getInt("visual.particles.count", 30);

            try {
                Particle particle = Particle.valueOf(particleType.toUpperCase());
                player.getWorld().spawnParticle(particle, player.getLocation().add(0, 1, 0), count, 0.5, 0.5, 0.5, 0.1);
            } catch (IllegalArgumentException e) {
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), count, 0.5, 0.5, 0.5,
                        0.1);
            }
        }

        if (plugin.getConfig().getBoolean("visual.sounds.enabled", true)) {
            String soundName = plugin.getConfig().getString("visual.sounds.hide-sound", "ENTITY_ENDERMAN_TELEPORT");
            float volume = (float) plugin.getConfig().getDouble("visual.sounds.hide-volume", 0.5);
            float pitch = (float) plugin.getConfig().getDouble("visual.sounds.hide-pitch", 1.5);

            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (IllegalArgumentException e) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, volume, pitch);
            }
        }
    }

    private void playShowEffects(Player player) {
        if (plugin.getConfig().getBoolean("visual.particles.enabled", true)) {
            String particleType = plugin.getConfig().getString("visual.particles.type", "CLOUD");
            int count = plugin.getConfig().getInt("visual.particles.count", 30);

            try {
                Particle particle = Particle.valueOf(particleType.toUpperCase());
                player.getWorld().spawnParticle(particle, player.getLocation().add(0, 1, 0), count, 0.5, 0.5, 0.5, 0.1);
            } catch (IllegalArgumentException e) {
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), count, 0.5, 0.5, 0.5,
                        0.1);
            }
        }

        if (plugin.getConfig().getBoolean("visual.sounds.enabled", true)) {
            String soundName = plugin.getConfig().getString("visual.sounds.show-sound", "ITEM_ARMOR_EQUIP_LEATHER");
            float volume = (float) plugin.getConfig().getDouble("visual.sounds.show-volume", 0.5);
            float pitch = (float) plugin.getConfig().getDouble("visual.sounds.show-pitch", 1.0);

            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (IllegalArgumentException e) {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, volume, pitch);
            }
        }
    }

    public void reload() {
        if (refreshTask != null) {
            refreshTask.cancel();
        }
        startRefreshTask();
    }

    public void handleDeath(Player player) {
        if (!plugin.getConfig().getBoolean("persistence.keep-on-death", false)) {
            if (isGearHidden(player)) {
                hiddenGearPlayers.remove(player.getUniqueId());
                plugin.getDataManager().setHidden(player.getUniqueId(), false);
            }
        }
    }

    public void handleRespawn(Player player) {
        if (isGearHidden(player)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                updateGearVisibility(player);
            }, 5L);
        }
    }
}
