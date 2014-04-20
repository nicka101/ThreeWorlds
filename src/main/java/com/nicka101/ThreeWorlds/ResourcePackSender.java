package com.nicka101.ThreeWorlds;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Nicka101 on 02/04/2014.
 */
public class ResourcePackSender extends BukkitRunnable {

    private final Player player;
    private final String resourcePackUrl;

    public ResourcePackSender(final Player player, final String resourcePackUrl){
        this.player = player;
        this.resourcePackUrl = resourcePackUrl;
    }

    @Override
    public void run(){
        if(player.isOnline()){
            player.setResourcePack(resourcePackUrl);
        }
    }
}
