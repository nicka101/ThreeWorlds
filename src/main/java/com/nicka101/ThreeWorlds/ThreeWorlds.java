package com.nicka101.ThreeWorlds;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Created by Nicka101 on 01/04/2014.
 */
public class ThreeWorlds extends JavaPlugin {

    private final Logger logger = this.getLogger();
    private final PlayerManager playerManager;

    @SuppressWarnings("unused")
    public ThreeWorlds(){
        log("Started Initialization of ThreeWorlds");
        playerManager = new PlayerManager(this);
    }

    public void onEnable(){
        playerManager.Init();
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("threeworlds").setExecutor(new ThreeWorldsExecutor(this));
        log("ThreeWorlds Initialization has completed");
    }

    public void onDisable(){
        playerManager.Shutdown();
        log("ThreeWorlds has been disabled");
    }

    protected PlayerManager getPlayerManager(){
        return playerManager;
    }

    protected void log(String message){
        logger.info(message);
    }

    protected void warning(String message){
        logger.warning(message);
    }
}
