package com.nicka101.ThreeWorlds.Generation;

import com.nicka101.ThreeWorlds.PlayerManager.WorldType;
import com.nicka101.ThreeWorlds.ThreeWorlds;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;

import java.util.*;

/**
 * Created by Nicka101 on 02/04/2014.
 */
public class OrePopulator extends BlockPopulator {

    private static final List<Material> gravityAffected = Collections.unmodifiableList(Arrays.asList(Material.SAND, Material.GRAVEL));

    private final Material replaceable;
    private final OreConfigManager configManager;

    public OrePopulator(ThreeWorlds plugin, WorldType worldType){
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
        configManager = new OreConfigManager(plugin, worldType);
    }

    public void populate(World world, Random random, Chunk chunk){
        for(int x = 1; x <= 16; x++){
            for(int y = 1; y <= 256; y++){
                for(int z = 1; z <= 16; z++){
                    Block b = chunk.getBlock(x, y, z);
                    if(b.getType() != replaceable)continue;
                    int rand = random.nextInt(50000);
                    OreConfigValue value = configManager.getOre(rand);
                    if(value != null) generateResourceChunk(b, random, value);
                }
            }
        }
    }

    private void generateResourceChunk(Block center, Random random, OreConfigValue value){
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
        if(gravityAffected.contains(value.Material)){
            for(int i = 0; i < inCircle.size(); i++){
                Block rel = inCircle.get(i).getRelative(BlockFace.DOWN);
                if(rel.isEmpty() || rel.isLiquid())inCircle.remove(i);
            }
        }
        if(inCircle.size() == 0) return;
        int trueCount = value.Size == 1 ? 1 : random.nextInt(value.Size - 1) + 1;
        while(trueCount > 0){
            Block b = inCircle.get(random.nextInt(inCircle.size()));
            b.setType(value.Material);
            inCircle.remove(b);
            if(inCircle.size() == 0) return;
            trueCount--;
        }
        inCircle.clear();
    }

    private static class OreConfigValue {
        public final Material Material;
        public final int Chance;
        public final int Size;

        protected OreConfigValue(Material material, int chance, int size){
            this.Material = material;
            this.Chance = chance;
            this.Size = size;
        }
    }

    private static class OreConfigManager {
        private final Set<OreConfigValue> configValues;
        private final int totalChance;

        protected OreConfigManager(ThreeWorlds plugin, WorldType worldType){
            HashSet<OreConfigValue> configValuesInit = new HashSet<>();
            ConfigurationSection config = plugin.getConfig().getConfigurationSection("options." + worldType.toString().toLowerCase() + ".generation.ores");
            Set<String> keys = config.getKeys(false);
            for(String key : keys){
                Material material = Material.getMaterial(key);
                if(material == null || !config.isSet(key + ".rate") || !config.isSet(key + ".size")){
                    plugin.warning("Failed to parse Ore configuration for the block type: " + key + ". Skipping...");
                    continue;
                }
                int size = config.getInt(key + ".size");
                int rate = config.getInt(key + ".rate");
                if(size == 0 || rate == 0){
                    plugin.warning("Failed to parse Ore configuration for the block type: " + key + ", 0 is not valid for size or rate! Skipping...");
                }
                configValuesInit.add(new OreConfigValue(material, rate, size));
            }
            //More optimizations can be performed on an immutable set and we can rely on our other counts
            configValues = Collections.unmodifiableSet(configValuesInit);
            int totalChance = 0;
            for(OreConfigValue value : configValues){
                totalChance += value.Chance;
            }
            if(totalChance > 50000) throw new RuntimeException(
                    "The Server was deliberately crashed to prevent an unreachable case. The specified combined ore chances exceed the maximum chance! Total: " + totalChance
            );
            this.totalChance = totalChance;
        }

        public OreConfigValue getOre(int random){
            if(random >= totalChance || random < 0)return null;
            int currentChance = 0;
            for(OreConfigValue value : configValues){
                if(random < currentChance + value.Chance) return value;
                currentChance += value.Chance;
            }
            return null;
        }
    }
}
