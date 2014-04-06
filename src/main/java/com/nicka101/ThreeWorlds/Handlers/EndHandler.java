package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Enderman;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class EndHandler extends WorldHandler {

    private final HashMap<UUID, Long> teleportCooldowns = new HashMap<>();
    private final long COOLDOWN;
    private final int DISTANCE;

    public EndHandler(ThreeWorlds plugin){
        super(plugin);
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("options.end.teleport");
        this.COOLDOWN = config.getLong("cooldown", 10000);
        this.DISTANCE = config.getInt("distance", 16);
    }

    @Override
    public void processPlayerAttackEvent(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Enderman)event.setCancelled(true);
    }

    @Override
    public void processEntityTargetEvent(EntityTargetEvent event){
        if(event.getEntity() instanceof Enderman)event.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void processPlayerInteractEvent(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_AIR)return;
        if(event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() != Material.OBSIDIAN)return;
        if(teleportCooldowns.containsKey(event.getPlayer().getUniqueId()) && teleportCooldowns.get(event.getPlayer().getUniqueId()) > System.currentTimeMillis()){
            event.getPlayer().sendMessage(ChatColor.RED + "That ability is on cooldown for " +
                    (((double)(teleportCooldowns.get(event.getPlayer().getUniqueId()) - System.currentTimeMillis())) / 1000d) +
                    " more Seconds");
            return;
        }
        teleportCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + COOLDOWN); //TODO: Cleanup on the hashmap?
        List<Block> targetBlocks = event.getPlayer().getLastTwoTargetBlocks(null, DISTANCE);
        if(targetBlocks.size() != 2){
            plugin.warning("Impossible case detected. Is this a correct Bukkit implementation?");
            return;
        }
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
        Location old = event.getPlayer().getLocation();
        Location target = targetBlocks.get(0).getLocation();
        target.setDirection(old.getDirection());
        target.setPitch(old.getPitch());
        target.setYaw(old.getYaw());
        target.setX(target.getX() + 0.5);
        target.setZ(target.getZ() + 0.5);
        event.getPlayer().teleport(target);
    }
}
