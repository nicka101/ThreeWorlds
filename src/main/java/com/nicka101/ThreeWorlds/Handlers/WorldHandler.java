package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.PlayerManager;
import com.nicka101.ThreeWorlds.ResourcePackSender;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class WorldHandler {

    private final ThreeWorlds plugin;

    public WorldHandler(ThreeWorlds plugin){
        this.plugin = plugin;
    }

    public void processDamageEvent(EntityDamageEvent event){
        //Do nothing
    }

    public void processPvpEvent(EntityDamageByEntityEvent event){
        if(!plugin.getPlayerManager().IsPlayerHostile((Player) event.getDamager(), (Player) event.getEntity())){
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
}
