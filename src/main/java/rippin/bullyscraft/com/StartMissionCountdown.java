package rippin.bullyscraft.com;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import rippin.bullyscraft.com.Configs.Config;

public class StartMissionCountdown {

    private FactionsMissions plugin;
    private int delay;
    private String broadcastMessage;

    public StartMissionCountdown(FactionsMissions plugin, int delay){
    this.plugin = plugin;
    this.delay = delay;
     broadcastMessage = ChatColor.translateAlternateColorCodes('&', Config.getConfig().getString("BroadcastMessage"));
    }

    public void startCountdown(){
        final int saveDelay = delay;
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                //announce messages soon?
                if (delay == 0 && Bukkit.getOnlinePlayers().size() > 0) {
                    if (MissionManager.getQueuedMissions().size() == 0) {
                        delay = saveDelay;
                    } else {
                        Mission m = MissionManager.getRandomQueuedMission();
                        m.start();
                        Double x = m.getSchematicLoc().getX();
                        Double y = m.getSchematicLoc().getY();
                        Double z = m.getSchematicLoc().getZ();
                        Bukkit.broadcastMessage(broadcastMessage.replace("%name%", m.getName())
                                .replace("%type%", m.getType().getValue()).replace("%coords%", "X: " + x.intValue() + " Y: " + y.intValue() + " Z: " + z.intValue()));
                        delay = saveDelay;
                    }
                }
                --delay;
            }
        }, 20L, 20L);
    }
}
