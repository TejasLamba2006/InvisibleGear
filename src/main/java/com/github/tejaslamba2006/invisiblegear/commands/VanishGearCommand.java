package com.github.tejaslamba2006.invisiblegear.commands;

import com.github.tejaslamba2006.invisiblegear.InvisibleGear;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Quick toggle command for gear visibility
 */
public class VanishGearCommand implements CommandExecutor {
    
    private final InvisibleGear plugin;
    
    public VanishGearCommand(InvisibleGear plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.colorize("&cThis command can only be used by players!"));
            return true;
        }
        
        if (!player.hasPermission("invisiblegear.use")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        plugin.getGearManager().toggleGear(player);
        return true;
    }
}
