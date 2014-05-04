package com.nicka101.ThreeWorlds;

import com.nicka101.ThreeWorlds.Events.EntityDamageByWaterEvent;
import com.nicka101.ThreeWorlds.Handlers.WorldHandler;
import net.minecraft.server.v1_7_R3.AxisAlignedBB;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MathHelper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Nicka101 on 21/04/2014.
 */
public class WaterCollisionTask extends BukkitRunnable {

    private final ThreeWorlds plugin;

    private static final int PARTICLES = 128;

    public WaterCollisionTask(final ThreeWorlds plugin){
        this.plugin = plugin;
    }

    @Override
    public void run(){
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if(!p.hasMetadata("waterCollisionBlock")) continue;
            Block source;
            WaterCollisionType collisionType;
            try {
                source = (Block)p.getMetadata("waterCollisionBlock").get(0).value();
                collisionType = (WaterCollisionType)p.getMetadata("waterCollisionType").get(0).value();
            } catch (ClassCastException e){
                continue;
            } catch (IndexOutOfBoundsException e){
                continue;
            }
            int damageTicks;
            try {
                if (p.hasMetadata("waterCollisionTicks")) damageTicks = p.getMetadata("waterCollisionTicks").get(0).asInt();
                else damageTicks = 0;
            } catch (IndexOutOfBoundsException e){
                damageTicks = 0;
            }
            if(damageTicks % 20 != 0){
                updateTicks(p, ++damageTicks);
                continue;
            }
            switch(collisionType){
                case DAMAGE:
                    damageByWater(p, source, 4);
                    break;
                case ENDER:
                    if(k(p)){
                        damageByWater(p, source, 1);
                    } else {
                        damageByWater(p, source, 5);
                    }
                    break;
            }
            updateTicks(p, 1);
        }
    }

    private void updateTicks(Player player, int ticks){
        if(player.hasMetadata("waterCollisionTicks")) player.removeMetadata("waterCollisionTicks", plugin);
        player.setMetadata("waterCollisionTicks", new FixedMetadataValue(plugin, ticks));
    }

    private void damageByWater(Player player, Block b, double damage){
        if(player.getGameMode() == GameMode.CREATIVE)return;
        EntityDamageByWaterEvent event = new EntityDamageByWaterEvent(b, player, EntityDamageEvent.DamageCause.CUSTOM, damage);
        plugin.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled())return;
        player.setLastDamageCause(event);
        player.damage(event.getDamage());
    }

    //CraftBukkit Enderman teleport code modified for use on a player in a plugin
    //Also refactored so the player isn't moved very quickly back and forward if the landing spot isn't safe
    private boolean k(Player p) {
        EntityPlayer minecraftPlayer = ((CraftPlayer)p).getHandle();
        Random random = new Random();
        double d3 = minecraftPlayer.locX;
        double d4 = minecraftPlayer.locY;
        double d5 = minecraftPlayer.locZ;
        double d0 = d3 + (random.nextDouble() - 0.5D) * 32.0D;
        double d1 = d4 + (double) (random.nextInt(32) - 16);
        double d2 = d5 + (random.nextDouble() - 0.5D) * 32.0D;
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);

        if (minecraftPlayer.world.isLoaded(i, j, k)) {
            boolean flag = false;

            while (!flag && j > 0) {
                net.minecraft.server.v1_7_R3.Block block = minecraftPlayer.world.getType(i, j - 1, k);

                if (block.getMaterial().isSolid()) {
                    flag = true;
                } else {
                    --d1;
                    --j;
                }
            }

            if (flag) {
                //Refactor to use the available bukkit functionality
                Location dest = p.getLocation().clone();
                dest.setX(d0);
                dest.setY(d1);
                dest.setZ(d2);
                //Use the more specific (and more relevant) PlayerTeleportEvent
                PlayerTeleportEvent teleport = new PlayerTeleportEvent(p,
                        p.getLocation(),
                        dest,
                        PlayerTeleportEvent.TeleportCause.PLUGIN);
                plugin.getServer().getPluginManager().callEvent(teleport);
                if (teleport.isCancelled()) {
                    return false;
                }

                Location to = teleport.getTo();
                //minecraftPlayer.setPosition(to.getX(), to.getY(), to.getZ());
                //Substitute the setPosition code without actually setting the position
                double locX = to.getX();
                double locY = to.getY();
                double locZ = to.getZ();
                float f = minecraftPlayer.width / 2.0F;
                float f1 = minecraftPlayer.length;

                //Create the boundingBox the setPosition call would have made
                AxisAlignedBB boundingBox = minecraftPlayer.boundingBox.clone().b(
                        locX - (double) f,
                        locY - (double) minecraftPlayer.height + (double) minecraftPlayer.V,
                        locZ - (double) f,
                        locX + (double) f,
                        locY - (double) minecraftPlayer.height + (double) minecraftPlayer.V + (double) f1,
                        locZ + (double) f);
                //Doesn't matter that minecraftPlayer has the wrong position information as this call tree never uses anything other than Class.isAssignableFrom
                if (minecraftPlayer.world.getCubes(minecraftPlayer, boundingBox).isEmpty() && !minecraftPlayer.world.containsLiquid(boundingBox)) {
                    //Replace setPosition with the speedhack-safe version for players
                    minecraftPlayer.playerConnection.teleport(to);
                    for (int l = 0; l < PARTICLES; ++l) {
                        double d6 = (double) l / ((double) PARTICLES - 1.0D);
                        float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                        float f3 = (random.nextFloat() - 0.5F) * 0.2F;
                        float f4 = (random.nextFloat() - 0.5F) * 0.2F;
                        double d7 = d3 + (minecraftPlayer.locX - d3) * d6 + (random.nextDouble() - 0.5D) * (double) minecraftPlayer.width * 2.0D;
                        double d8 = d4 + (minecraftPlayer.locY - d4) * d6 + random.nextDouble() * (double) minecraftPlayer.length;
                        double d9 = d5 + (minecraftPlayer.locZ - d5) * d6 + (random.nextDouble() - 0.5D) * (double) minecraftPlayer.width * 2.0D;

                        minecraftPlayer.world.addParticle("portal", d7, d8, d9, (double) f2, (double) f3, (double) f4);
                    }

                    minecraftPlayer.world.makeSound(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
                    minecraftPlayer.makeSound("mob.endermen.portal", 1.0F, 1.0F);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
