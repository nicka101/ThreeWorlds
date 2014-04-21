package com.nicka101.ThreeWorlds.Handlers;

import com.nicka101.ThreeWorlds.Events.EntityDamageByWaterEvent;
import com.nicka101.ThreeWorlds.ResourcePackSender;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import net.minecraft.server.v1_7_R3.AxisAlignedBB;
import net.minecraft.server.v1_7_R3.MathHelper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

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

    public void processMoveEvent(PlayerMoveEvent event){
        //Do Nothing
    }

    protected void sendResourcePack(World.Environment environment, Player player){
        if(getResourcePackUrl(environment) != null){
            new ResourcePackSender(player, getResourcePackUrl(environment)).runTask(plugin);
        }
    }

    @Deprecated
    protected void TouchingWater(Player p){
        Block b =  AABBIntersectsWater(p.getWorld(), ((CraftPlayer)p).getHandle().boundingBox);
        if(b == null && p.hasMetadata("waterDamageBlock")){
            p.removeMetadata("waterDamageBlock", plugin);
            p.removeMetadata("waterDamageTicks", plugin);
        }
        else if(b != null){
            if(p.hasMetadata("waterDamageBlock"))p.removeMetadata("waterDamageBlock", plugin);
            p.setMetadata("waterDamageBlock", new FixedMetadataValue(plugin, b));
        }
    }

    @Deprecated
    protected Block AABBIntersectsWater(World world, AxisAlignedBB boundingBox){
        int i = MathHelper.floor(boundingBox.a);
        int j = MathHelper.floor(boundingBox.d + 1.0D);
        int k = MathHelper.floor(boundingBox.b);
        int l = MathHelper.floor(boundingBox.e + 1.0D);
        int i1 = MathHelper.floor(boundingBox.c);
        int j1 = MathHelper.floor(boundingBox.f + 1.0D);

        net.minecraft.server.v1_7_R3.World minecraftWorld = ((CraftWorld)world).getHandle();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    if (minecraftWorld.getType(k1, l1, i2).getMaterial() == net.minecraft.server.v1_7_R3.Material.WATER) {
                        return world.getBlockAt(k1, l1, i2);
                    }
                }
            }
        }

        return null;
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
