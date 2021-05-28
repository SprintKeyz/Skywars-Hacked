package com.sprintkeyz.swhacks;

import com.sprintkeyz.swhacks.events.SWHacksEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.image.renderable.RenderableImageProducer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SWHacks extends JavaPlugin {

    public File userinf;
    public File path;

    @Override
    public void onEnable() {
        // check stuff
        //checkYLevel();


        // update gamerules
        getServer().getWorld("world").setGameRuleValue("doDaylightCycle", "false");
        getServer().getWorld("world").setGameRuleValue("doWeatherCycle", "false");
        getServer().getWorld("world").setGameRuleValue("doMobSpawning", "false");
        getServer().getWorld("world").setGameRuleValue("doFireTick", "false");
        getLogger().info("GameRules set!");

        // set up listeners
        Bukkit.getPluginManager().registerEvents(new SWHacksEvents(this), this);

        // register commands
        getCommand("api").setExecutor(new SWHacksEvents(this));
        getCommand("resetmap").setExecutor(new SWHacksEvents(this));
        getCommand("resetcages").setExecutor(new SWHacksEvents(this));
        getCommand("resetblocks").setExecutor(new SWHacksEvents(this));

        // end of enable
        getLogger().info("---------------------------------------------------------------------------");
        getLogger().info("Plugin enabled!");
        getLogger().info("There's a good chance that you aren't subscribed to SprintKeyz!");
        getLogger().info("He worked hard on this plugin so consider subscribing to him at youtube.com/sprintkeyz! <3");
        getLogger().info("---------------------------------------------------------------------------");
    }

    public void checkYLevel() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().getY() <= 1) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.ADVENTURE);
                        player.setAllowFlight(true);
                        SWHacksEvents.deadPlayers.add(player);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                        for (Player pl1 : Bukkit.getOnlinePlayers()) {
                            if (pl1 != player) {
                                pl1.hidePlayer(player);
                            }
                        }
                    }
                }
            }
        }, 0L, 5L);
    }
}
