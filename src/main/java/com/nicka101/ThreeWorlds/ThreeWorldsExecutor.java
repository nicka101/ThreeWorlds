package com.nicka101.ThreeWorlds;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Created by Nicka101 on 02/04/2014.
 */
public class ThreeWorldsExecutor implements CommandExecutor {

    private final ThreeWorlds plugin;
    protected final Inventory allegianceChangeInv;

    protected ThreeWorldsExecutor(final ThreeWorlds plugin){
        this.plugin = plugin;

        allegianceChangeInv = plugin.getServer().createInventory(null, 9, "Allegiance Selection");
        ItemStack overworldSelect = new ItemStack(Material.GRASS, 1);
        ItemMeta i = overworldSelect.getItemMeta();
        i.setDisplayName(ChatColor.GREEN + "Overworld Allegiance");
        overworldSelect.setItemMeta(i);
        ItemStack netherSelect = new ItemStack(Material.NETHERRACK, 1);
        i = netherSelect.getItemMeta();
        i.setDisplayName(ChatColor.RED + "Nether Allegiance");
        netherSelect.setItemMeta(i);
        ItemStack endSelect = new ItemStack(Material.ENDER_STONE, 1);
        i = endSelect.getItemMeta();
        i.setDisplayName(ChatColor.DARK_PURPLE + "End Allegiance");
        endSelect.setItemMeta(i);
        allegianceChangeInv.setContents(new ItemStack[]{ null, null, null, overworldSelect, netherSelect, endSelect });
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
        if(!(sender instanceof Player))return true;
        Player player = (Player)sender;
        if(args.length == 0)return false;
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("select")){
                if(plugin.getPlayerManager().IsPlayerInWorld(player)){
                    player.sendMessage(ChatColor.RED + "You have already selected an allegiance!");
                    return true;
                }
                player.openInventory(allegianceChangeInv);
                return true;
            }
        }
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
            } else if(args[0].equalsIgnoreCase("admin")){
                if(args[1].equalsIgnoreCase("warp-test")){
                    player.sendMessage(ChatColor.GOLD + "Commencing Warp!");
                    Pig piggy = (Pig)player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.PIG);
                    piggy.setPassenger(player);
                    piggy.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 255, 255));
                    piggy.setVelocity(new Vector(0, 30, 0));
                }
            }
        }
        return false;
    }
}
