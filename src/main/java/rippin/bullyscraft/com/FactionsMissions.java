package rippin.bullyscraft.com;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import rippin.bullyscraft.com.Configs.Config;
import rippin.bullyscraft.com.Configs.ConfigManager;
import rippin.bullyscraft.com.Configs.MobsConfig;

import java.util.logging.Logger;

public class FactionsMissions extends JavaPlugin{
    public static FactionsMissions instance;
    public final Logger logger = Logger.getLogger("Minecraft");

    public void onEnable() {
    instance = this;
    ConfigManager.generateConfigs(this);
    MobsManager.loadMobs(this);
    MissionManager.loadMissions(this);
    this.getCommand("bullymission").setExecutor(new MissionCommands(this));
    getServer().getPluginManager().registerEvents(new MissionListeners(this), this);
       //check if you want to paste the schcematics

        this.getServer().getScheduler().runTaskLater(this, new Runnable() {
        public void run() {
        MissionManager.revertMissionsIfCrashed();
               }
    }, 20L);
        if (Config.getConfig().getInt("Mission-Delay") > 0 && Config.getConfig().getBoolean("Mission-Task")) {
        new StartMissionCountdown(this, Config.getConfig().getInt("Mission-Delay")).startCountdown();
        }
        //set night time
        Utilss.setNight(this, MissionManager.getMissionWorld());
    }

    public void onDisable() {
       instance = null;
       MobsManager.clearReplaceEntUUIDs();
        MobsConfig.saveFile();
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldEditPlugin) plugin;
    }

    public Plugin getAsyncWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("AsyncWorldEdit");

        // AsyncWorldEdit is not loaded.
        if (plugin == null) {
            return null; // Maybe you want throw an exception instead
        }

        return plugin;
    }
}
