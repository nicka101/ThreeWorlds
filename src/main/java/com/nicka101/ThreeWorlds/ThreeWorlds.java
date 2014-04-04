package com.nicka101.ThreeWorlds;

import com.nicka101.ThreeWorlds.Generation.EndGenerator;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class ThreeWorlds extends JavaPlugin {

    private final Logger logger = this.getLogger();
    private final PlayerManager playerManager;
    protected final String HOLDING_WORLD_NAME = "holding_world";

    @SuppressWarnings("unused")
    public ThreeWorlds(){
        log("Started Initialization of ThreeWorlds");
        playerManager = new PlayerManager(this);
    }

    public void onEnable(){
        this.saveDefaultConfig(); //Doesn't matter if it already exists, this won't overwrite
        playerManager.Init();
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("threeworlds").setExecutor(new ThreeWorldsExecutor(this));
        log("ThreeWorlds Initialization has completed");
    }

    public void onDisable(){
        playerManager.Shutdown();
        log("ThreeWorlds has been disabled");
    }

    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    protected void log(String message){
        logger.info(message);
    }

    protected void warning(String message){
        logger.warning(message);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String world, String id){
        if(id.equalsIgnoreCase("end")){
            return new EndGenerator(this);
        }
        return null;
    }
}
