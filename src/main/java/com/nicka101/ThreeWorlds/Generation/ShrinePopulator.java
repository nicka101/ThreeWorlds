package com.nicka101.ThreeWorlds.Generation;

import com.nicka101.ThreeWorlds.PlayerManager;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * Created by Nicka101 on 19/04/2014.
 */
public class ShrinePopulator extends BlockPopulator {

    private final ThreeWorlds plugin;
    private final PlayerManager.WorldType environment;
    private Location center = null;
    private static final int RADIUS = 15;
    private static final int INNER_RADIUS = 2;

    public ShrinePopulator(final ThreeWorlds plugin, final PlayerManager.WorldType Environment){
        this.plugin = plugin;
        this.environment = Environment;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk){
        Chunk spawnChunk = world.getSpawnLocation().getChunk();
        if(distance(spawnChunk.getX(), spawnChunk.getZ(), chunk.getX(), chunk.getZ()) > 3)return;
        if(center == null){
            center = spawnChunk.getBlock(8, 0, 8).getLocation();
            world.setSpawnLocation(center.getBlockX(), 64, center.getBlockZ());
            plugin.log("Spawn Location for World " + world.getName() + " set to: " + center.getBlockX() + ", " + center.getBlockZ());
        }
        int chunkX = chunk.getX() * 16;
        int chunkZ = chunk.getZ() * 16;
        for(int x = 0; x < 16; x++){
            for(int y = 0; y < world.getMaxHeight() - 1; y++){
                for(int z = 0; z < 16; z++){
                    double dist = distanceFromCenter(chunkX + x, chunkZ + z);
                    if(dist > RADIUS-1 && dist <= RADIUS) chunk.getBlock(x, y, z).setType(Material.SMOOTH_BRICK);
                    else if(dist <= RADIUS-1){
                        if((dist > INNER_RADIUS && y % 11 == 0) || y == 0)genFloor(chunk, x, y, z);
                        else if(dist <= INNER_RADIUS) {
                            if (y % 11 == 1 || y % 11 == 2) chunk.getBlock(x, y, z).setType(Material.AIR);
                            else if(dist > INNER_RADIUS - 1) chunk.getBlock(x, y, z).setType(Material.GLASS);
                            else if(y == 4 || y == 5 || y == 6) chunk.getBlock(x, y, z).setType(Material.STATIONARY_WATER);
                            else if(y == 3)setSign(chunk.getBlock(x, y, z));
                            else chunk.getBlock(x, y, z).setType(Material.AIR);
                        } else {
                            Block b = chunk.getBlock(x, y, z);
                            if(b.getType() != Material.GOLD_PLATE && b.getType() != Material.IRON_PLATE) b.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }


    private double distance(double x1, double z1, double x2, double z2){
        return Math.sqrt((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2));
    }

    private double distanceFromCenter(int x, int z){
        return distance(x, z, center.getX(), center.getZ());
    }

    @SuppressWarnings("deprecation")
    private void setSign(Block b){
        b.setType(Material.WALL_SIGN);
        b.setData((byte)3);
    }

    private void genFloor(Chunk chunk, int x, int y, int z){
        int chunkX = (chunk.getX() * 16) + x;
        int chunkZ = (chunk.getZ() * 16) + z;
        if((chunkX == center.getBlockX() || chunkZ == center.getBlockZ()) &&
                (Math.abs(chunkX - center.getBlockX()) == ((RADIUS/2) + (y % 2 == 0 ? 1 : 0)) ||  Math.abs(chunkZ - center.getBlockZ()) == ((RADIUS/2) + (y % 2 == 0 ? 1 : 0)))) {
            chunk.getBlock(x, y, z).setType(Material.SPONGE);
            chunk.getBlock(x, y + 1, z).setType(Material.GOLD_PLATE);
        } else if(distanceFromCenter(chunkX, chunkZ) == RADIUS - 1){
            chunk.getBlock(x, y, z).setType(Material.SPONGE);
            chunk.getBlock(x, y + 1, z).setType(Material.IRON_PLATE);
        } else {
            chunk.getBlock(x, y, z).setType(x % 4 == 0 && z % 4 == 0? Material.GLOWSTONE : Material.SMOOTH_BRICK);
        }
    }
}
