package com.nicka101.ThreeWorlds.Generation;

import com.nicka101.ThreeWorlds.PlayerManager.WorldType;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BlockPopulator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nicka101 on 02/04/2014.
 */
public class OrePopulator extends BlockPopulator {

    private final ThreeWorlds plugin;
    private final String worldType;
    private final Material replaceable;

    public OrePopulator(ThreeWorlds plugin, WorldType worldType){
        this.plugin = plugin;
        this.worldType = worldType.toString().toLowerCase();
        switch (worldType){
            case NETHER:
                replaceable = Material.NETHERRACK;
                break;
            case END:
                replaceable = Material.ENDER_STONE;
                break;
            default:
                replaceable = Material.STONE; //Should never occur but handle it anyway
        }
    }

    public void populate(World world, Random random, Chunk chunk){
        FileConfiguration config = plugin.getConfig();
        int coalChance = config.getInt("options." + worldType + ".ores.rate.coal", 20);
        int ironChance = config.getInt("options." + worldType + ".ores.rate.iron", 8);
        int ironMin = coalChance + 1;
        int goldChance = config.getInt("options." + worldType + ".ores.rate.gold", 4);
        int goldMin = coalChance + ironChance + 1;
        int redstoneChance = config.getInt("options." + worldType + ".ores.rate.gold", 9);
        int redstoneMin = goldMin + goldChance + 1;
        int lapisChance = config.getInt("options." + worldType + ".ores.rate.gold", 3);
        int lapisMin = redstoneMin + redstoneChance + 1;
        int diamondChance = config.getInt("options." + worldType + ".ores.rate.diamond", 1);
        int diamondMin = lapisMin + lapisChance + 1;

        int coalSize = config.getInt("options." + worldType + ".ores.size.coal", 6);
        int ironSize = config.getInt("options." + worldType + ".ores.size.iron", 4);
        int goldSize = config.getInt("options." + worldType + ".ores.size.gold", 4);
        int redstoneSize = config.getInt("options." + worldType + ".ores.size.redstone", 9);
        int lapisSize = config.getInt("options." + worldType + ".ores.size.lapis", 3);
        int diamondSize = config.getInt("options." + worldType + ".ores.size.diamond", 4);

        for(int x = 1; x <= 16; x++){
            for(int y = 1; y <= 256; y++){
                for(int z = 1; z <= 16; z++){
                    Block b = chunk.getBlock(x, y, z);
                    if(b.getType() != replaceable)continue;
                    int rand = random.nextInt(50000);
                    if(rand >= 0 && rand < coalChance){
                        generateResourceChunk(b, random, Material.COAL_ORE, coalSize);
                    } else if(rand >= ironMin && rand < ironMin + ironChance){
                        generateResourceChunk(b, random, Material.IRON_ORE, ironSize);
                    } else if(rand >= goldMin && rand < goldMin + goldChance) {
                        generateResourceChunk(b, random, Material.GOLD_ORE, goldSize);
                    } else if(rand >= redstoneMin && rand < redstoneMin + redstoneChance) {
                        generateResourceChunk(b, random, Material.REDSTONE_ORE, redstoneSize);
                    } else if(rand >= lapisMin && rand < lapisMin + lapisChance){
                        generateResourceChunk(b, random, Material.LAPIS_ORE, lapisSize);
                    } else if(rand >= diamondMin && rand < diamondMin + diamondChance){
                        generateResourceChunk(b, random, Material.DIAMOND_ORE, diamondSize);
                    }
                }
            }
        }
    }

    private void generateResourceChunk(Block center, Random random, Material mat, int count){
        ArrayList<Block> inCircle = new ArrayList<>();
        for(int x = center.getX() - 2; x <= center.getX() + 2; x++){
            for(int y = center.getY() - 2; y <= center.getY() + 2; y++){
                for(int z = center.getZ() - 2; z <= center.getZ(); z++){
                    if(Math.pow(center.getX() - x, 2) + Math.pow(center.getY() - y, 2) + Math.pow(center.getZ() - z, 2) <= 8){
                        if(center.getWorld().getBlockAt(x, y, z).getType() == replaceable){
                            inCircle.add(center.getWorld().getBlockAt(x, y, z));
                        }
                    }
                }
            }
        }
        if(inCircle.size() == 0)return;
        int trueCount = count == 1 ? 1 : random.nextInt(count - 1) + 1;
        while(trueCount > 0){
            Block b = inCircle.get(random.nextInt(inCircle.size()));
            b.setType(mat);
            inCircle.remove(b);
            if(inCircle.size() == 0)return;
            trueCount--;
        }
    }
}
