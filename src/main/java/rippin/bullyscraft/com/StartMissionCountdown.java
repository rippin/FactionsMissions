package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import rippin.bullyscraft.com.Configs.Config;

public class StartMissionCountdown {

    private FactionsMissions plugin;
    private int delay;
    private String broadcastMessage;
    public StartMissionCountdown(FactionsMissions plugin, int delay){
    this.plugin = plugin;
    this.delay = (delay*20);
    broadcastMessage = ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("BroadcastMessage"));
    }

    public void startCountdown(){
        final int saveDelay = delay;
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BukkitRunnable() {
            @Override
            public void run() {
                if (delay == 0){
                Mission m = MissionManager.getRandomQueuedMission();
                m.start();
                 double x = m.getSchematicLoc().getX();
                 double y = m.getSchematicLoc().getY();
                 double z = m.getSchematicLoc().getZ();
                    Bukkit.broadcastMessage(broadcastMessage.replace("%name%", m.getName())
                            .replace("%type%", m.getType().getValue()).replace("%coords%", "X: " + x + "Y: " + y + "Z: " + z));
                    delay = saveDelay;
                }
                --delay;
            }
        }, 20L, 20L);
    }
}
