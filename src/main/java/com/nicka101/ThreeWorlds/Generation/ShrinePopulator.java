package com.nicka101.ThreeWorlds.Generation;

import com.nicka101.ThreeWorlds.PlayerManager;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import com.nicka101.ThreeWorlds.Util.BlockUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
        if(center == null){
            center = spawnChunk.getBlock(8, 0, 8).getLocation();
        }
        if(distance(spawnChunk.getX(), spawnChunk.getZ(), chunk.getX(), chunk.getZ()) > 3)return;
        int chunkX = chunk.getX() * 16;
        int chunkZ = chunk.getZ() * 16;
        for(int x = 0; x < 16; x++){
            for(int y = 0; y < world.getMaxHeight() - 1; y++){
                for(int z = 0; z < 16; z++){
                    double dist = distanceFromCenter(chunkX + x, chunkZ + z);
                    if(dist > RADIUS) continue;
                    int realX = chunkX + x;
                    int realZ = chunkZ + z;
                    if(dist > RADIUS-1 && dist <= RADIUS) BlockUtil.setBlockFastUnsafe(world, realX, y, realZ, Material.SMOOTH_BRICK);
                    else if(dist <= RADIUS-1){
                        if((dist > INNER_RADIUS && y % 11 == 0))genFloor(world, x, y, z);
                        else if(dist <= INNER_RADIUS) {
                            if (y % 11 == 1 || y % 11 == 2 || y % 11 == 3) BlockUtil.setBlockFastUnsafe(world, realX, y, realZ, Material.AIR);
                            else if(dist > INNER_RADIUS - 1){
                                BlockUtil.setBlockFastUnsafe(world, realX, y, realZ, y % 11 == 0 ? Material.SPONGE : Material.GLASS);
                            }
                            else if(y == 0) BlockUtil.setBlockFastUnsafe(world, realX, y, realZ, Material.SPONGE);
                            else BlockUtil.setBlockFastUnsafe(world, realX, y, realZ, Material.AIR);
                        } else {
                            if(BlockUtil.getTypeIdUnsafe(world, realX, y, realZ) != Material.GOLD_PLATE.getId() &&
                                    BlockUtil.getTypeIdUnsafe(world, realX, y, realZ) != Material.IRON_PLATE.getId()) BlockUtil.setBlockFastUnsafe(world, realX, y, realZ, Material.AIR);
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

    private void genFloor(World world, int x, int y, int z){
        if((x == center.getBlockX() || z == center.getBlockZ()) &&
                (Math.abs(x - center.getBlockX()) == ((RADIUS/2) + (y % 2 == 0 ? 1 : 0)) ||  Math.abs(z - center.getBlockZ()) == ((RADIUS/2) + (y % 2 == 0 ? 1 : 0)))) {
            BlockUtil.setBlockFastUnsafe(world, x, y, z, Material.SPONGE);
            BlockUtil.setBlockFastUnsafe(world, x, y + 1, z, Material.GOLD_PLATE);
        } else if(distanceFromCenter(x, z) == RADIUS - 1){
            BlockUtil.setBlockFastUnsafe(world, x, y, z, Material.SPONGE);
            BlockUtil.setBlockFastUnsafe(world, x, y + 1, z, Material.IRON_PLATE);
        } else {
            BlockUtil.setBlockFastUnsafe(world, x, y, z, x % 4 == 0 && z % 4 == 0 ? Material.GLOWSTONE : Material.SMOOTH_BRICK);
        }
    }
}
