package com.github.tejaslamba2006.invisiblegear.commands;

import com.github.tejaslamba2006.invisiblegear.InvisibleGear;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InvisibleGearCommand implements CommandExecutor, TabCompleter {

    private final InvisibleGear plugin;

    public InvisibleGearCommand(InvisibleGear plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                handleToggle(player, null);
            } else {
                sendHelp(sender);
            }
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "toggle" -> {
                if (sender instanceof Player player) {
                    if (args.length >= 2) {
                        handleToggle(player, args[1]);
                    } else {
                        handleToggle(player, null);
                    }
                } else {
                    sender.sendMessage(plugin.colorize("&cThis command can only be used by players!"));
                }
            }
            case "hide" -> {
                if (sender instanceof Player player) {
                    handleHide(player, args.length >= 2 ? args[1] : null);
                } else {
                    sender.sendMessage(plugin.colorize("&cThis command can only be used by players!"));
                }
            }
            case "show" -> {
                if (sender instanceof Player player) {
                    handleShow(player, args.length >= 2 ? args[1] : null);
                } else {
                    sender.sendMessage(plugin.colorize("&cThis command can only be used by players!"));
                }
            }
            case "status" -> {
                if (sender instanceof Player player) {
                    handleStatus(player, args.length >= 2 ? args[1] : null);
                } else {
                    sender.sendMessage(plugin.colorize("&cThis command can only be used by players!"));
                }
            }
            case "reload" -> handleReload(sender);
            case "help" -> sendHelp(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void handleToggle(Player sender, String targetName) {
        Player target = sender;

        if (targetName != null) {
            if (!sender.hasPermission("invisiblegear.others")) {
                sender.sendMessage(plugin.getMessage("no-permission"));
                return;
            }

            target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(plugin.getMessage("player-not-found"));
                return;
            }
        }

        boolean nowHidden = plugin.getGearManager().toggleGear(target);

        if (!target.equals(sender)) {
            if (nowHidden) {
                sender.sendMessage(plugin.getMessage("target-gear-hidden", "{player}", target.getName()));
                target.sendMessage(plugin.getMessage("your-gear-hidden-by", "{player}", sender.getName()));
            } else {
                sender.sendMessage(plugin.getMessage("target-gear-shown", "{player}", target.getName()));
                target.sendMessage(plugin.getMessage("your-gear-shown-by", "{player}", sender.getName()));
            }
        }
    }

    private void handleHide(Player sender, String targetName) {
        Player target = sender;

        if (targetName != null) {
            if (!sender.hasPermission("invisiblegear.others")) {
                sender.sendMessage(plugin.getMessage("no-permission"));
                return;
            }

            target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(plugin.getMessage("player-not-found"));
                return;
            }
        }

        if (plugin.getGearManager().isGearHidden(target)) {
            if (target.equals(sender)) {
                sender.sendMessage(plugin.getMessage("already-hidden"));
            } else {
                sender.sendMessage(plugin.colorize("&c" + target.getName() + "'s gear is already hidden!"));
            }
            return;
        }

        plugin.getGearManager().hideGear(target);

        if (!target.equals(sender)) {
            sender.sendMessage(plugin.getMessage("target-gear-hidden", "{player}", target.getName()));
            target.sendMessage(plugin.getMessage("your-gear-hidden-by", "{player}", sender.getName()));
        }
    }

    private void handleShow(Player sender, String targetName) {
        Player target = sender;

        if (targetName != null) {
            if (!sender.hasPermission("invisiblegear.others")) {
                sender.sendMessage(plugin.getMessage("no-permission"));
                return;
            }

            target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(plugin.getMessage("player-not-found"));
                return;
            }
        }

        if (!plugin.getGearManager().isGearHidden(target)) {
            if (target.equals(sender)) {
                sender.sendMessage(plugin.getMessage("already-shown"));
            } else {
                sender.sendMessage(plugin.colorize("&c" + target.getName() + "'s gear is already visible!"));
            }
            return;
        }

        plugin.getGearManager().showGear(target);

        if (!target.equals(sender)) {
            sender.sendMessage(plugin.getMessage("target-gear-shown", "{player}", target.getName()));
            target.sendMessage(plugin.getMessage("your-gear-shown-by", "{player}", sender.getName()));
        }
    }

    private void handleStatus(Player sender, String targetName) {
        Player target = sender;

        if (targetName != null) {
            target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(plugin.getMessage("player-not-found"));
                return;
            }
        }

        boolean hidden = plugin.getGearManager().isGearHidden(target);

        if (target.equals(sender)) {
            sender.sendMessage(plugin.getMessage(hidden ? "status-hidden" : "status-shown"));
        } else {
            String status = hidden ? "&aHIDDEN" : "&eVISIBLE";
            sender.sendMessage(plugin.colorize("&7" + target.getName() + "'s gear: " + status));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("invisiblegear.admin")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return;
        }

        plugin.reload();
        sender.sendMessage(plugin.getMessage("reload"));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.colorize("&8&m----------&r &b&lInvisibleGear &8&m----------"));
        sender.sendMessage(plugin.colorize("&b/ig &8- &7Toggle your gear visibility"));
        sender.sendMessage(plugin.colorize("&b/ig toggle [player] &8- &7Toggle gear visibility"));
        sender.sendMessage(plugin.colorize("&b/ig hide [player] &8- &7Hide gear"));
        sender.sendMessage(plugin.colorize("&b/ig show [player] &8- &7Show gear"));
        sender.sendMessage(plugin.colorize("&b/ig status [player] &8- &7Check gear status"));
        sender.sendMessage(plugin.colorize("&b/ig reload &8- &7Reload configuration"));
        sender.sendMessage(plugin.colorize("&8&m----------------------------------------"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions = Arrays.asList("toggle", "hide", "show", "status", "reload", "help");
            return filter(completions, args[0]);
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "toggle", "hide", "show", "status" -> {
                    if (sender.hasPermission("invisiblegear.others")) {
                        return filter(getOnlinePlayerNames(), args[1]);
                    }
                }
            }
        }

        return completions;
    }

    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> filter(List<String> list, String prefix) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
