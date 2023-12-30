//©️Lukas Pellny | 2023
package de.lukasdev.dailyreward;

import de.lukasdev.dailyreward.IPChecker.MultiaccountSystem;
import de.lukasdev.dailyreward.commands.DailyRewardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DailyReward extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("RewardSystem-1.0 wurde aktivert.");
        getCommand("daily").setExecutor(new DailyRewardManager());
        Bukkit.getPluginManager().registerEvents(new MultiaccountSystem(), this);
        Bukkit.getPluginManager().registerEvents(new DailyRewardManager(), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("RewardSystem-1.0 wurde deaktiviert.");


    }
}
