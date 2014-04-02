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
                        break;
                }
                return true;
            }
        }
        return false;
    }
}
