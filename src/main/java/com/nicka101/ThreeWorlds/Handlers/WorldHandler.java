package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.ResourcePackSender;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class WorldHandler {

    protected final ThreeWorlds plugin;

    public WorldHandler(ThreeWorlds plugin){
        this.plugin = plugin;
    }

    public void processDamageEvent(EntityDamageEvent event){
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL && probableSpongeLanding(event.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN))){
            event.setCancelled(true);
        }
    }

    public void processPlayerAttackEvent(EntityDamageByEntityEvent event){
        //Do Nothing
    }

    public void processPlayerAttackedEvent(EntityDamageByEntityEvent event){
        //Do Nothing
    }

    public void processEntityTargetEvent(EntityTargetEvent event){
        event.setCancelled(true);
    }

    public void processWorldChange(PlayerChangedWorldEvent event){
        sendResourcePack(event.getPlayer().getWorld().getEnvironment(), event.getPlayer());
    }

    public void processLoginEvent(PlayerJoinEvent event){
        sendResourcePack(event.getPlayer().getWorld().getEnvironment(), event.getPlayer());
    }

    public void processPlayerInteractEvent(PlayerInteractEvent event){
        //Do Nothing
    }

    public void processBlockBreak(BlockBreakEvent event){
        //Do Nothing
    }

    protected void sendResourcePack(World.Environment environment, Player player){
        if(getResourcePackUrl(environment) != null){
            new ResourcePackSender(player, getResourcePackUrl(environment)).runTask(plugin);
        }
    }

    private String getResourcePackUrl(World.Environment environment){
        switch (environment){
            case THE_END:
                return "http://dl.cloudbro.net/ThreeWorldsEnd.zip";
            case NETHER:
                return "http://dl.cloudbro.net/ThreeWorldsNether.zip";
            default:
                return null;
        }
    }

    private boolean probableSpongeLanding(Block landingBlock){
        return landingBlock.getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.NORTH).getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.SOUTH).getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.EAST).getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.WEST).getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.SPONGE ||
                landingBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.SPONGE;
    }
}
