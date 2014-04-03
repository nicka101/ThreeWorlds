package com.nicka101.ThreeWorlds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Nicka101 on 02/04/2014.
 */
public class ThreeWorldsExecutor implements CommandExecutor {

    private final ThreeWorlds plugin;

    protected ThreeWorldsExecutor(ThreeWorlds plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player))return true;
        Player player = (Player)sender;
        if(args.length == 0)return false;
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("select")){
                if(plugin.getPlayerManager().IsPlayerInWorld(player)){
                    player.sendMessage(ChatColor.RED + "You have already selected an allegiance!");
                    return true;
                }
                switch(args[1].toLowerCase()){
                    case "overworld":
                        plugin.getPlayerManager().AddPlayerToWorld(PlayerManager.WorldType.OVERWORLD, player);
                        break;
                    case "nether":
                        plugin.getPlayerManager().AddPlayerToWorld(PlayerManager.WorldType.NETHER, player);
                        break;
                    case "end":
                        plugin.getPlayerManager().AddPlayerToWorld(PlayerManager.WorldType.END, player);
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "/" + label + " select <overworld|nether|end>");
                        return true;
                }
                player.sendMessage(ChatColor.GOLD + "Welcome to the winning team!");
                return true;
            } else if(args[0].equalsIgnoreCase("change")){
                //TODO: Charge something to transfer allegiance
                if(!plugin.getPlayerManager().IsPlayerInWorld(player)){
                    player.sendMessage(ChatColor.RED + "You have not yet selected an allegiance, so it is impossible to change. Try: ");
                    player.sendMessage(ChatColor.BLUE + "/" + label + " select " + args[1]);
                    return true;
                }
                switch(args[1].toLowerCase()){
                    case "overworld":
                        plugin.getPlayerManager().ChangePlayerWorld(PlayerManager.WorldType.OVERWORLD, player);
                        break;
                    case "nether":
                        plugin.getPlayerManager().ChangePlayerWorld(PlayerManager.WorldType.NETHER, player);
                        break;
                    case "end":
                        plugin.getPlayerManager().ChangePlayerWorld(PlayerManager.WorldType.END, player);
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "/" + label + " select <overworld|nether|end>");
                        return true;
                }
                player.sendMessage(ChatColor.GOLD + "At least you selected the right team this time!");
                return true;
            }
        }
        return false;
    }
}
