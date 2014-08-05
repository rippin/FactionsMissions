package rippin.bullyscraft.com;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import rippin.bullyscraft.com.Configs.Config;

import java.util.logging.Logger;

public class FactionsMissions extends JavaPlugin{
    public static FactionsMissions instance;
    public final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
    instance = this;
    new StartMissionCountdown(this, Config.getConfig().getInt("Mission-Delay"));
    }

    @Override
    public void onDisable() {
    instance = null;
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
}
